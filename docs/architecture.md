# Eventing Gateway Architecture

## Components

### 1. Data Plane (Kroxylicious Proxy)
The data plane is powered by **Kroxylicious**, utilizing a "Bootstrap Router" model to support multi-cluster routing with per-backend mTLS.

- **Bootstrap Router Virtual Cluster**: All clients initially connect to a single "Bootstrap" virtual cluster.
- **Backend Virtual Clusters**: Each backend Kafka cluster (e.g., separate AWS MSK clusters) is represented as a separate Kroxylicious `virtualCluster`. This allows each backend to have its own unique mTLS configuration, including certificates and truststores.
- **MultiClusterRoutingFilter**: This filter resides in the Bootstrap Virtual Cluster. It intercepts `MetadataResponse` messages. Based on the topic-to-cluster mapping, it rewrites the broker endpoints in the response to point the client at the appropriate Backend Virtual Cluster endpoint.
- **Dynamic Configuration**: The gateway configuration is dynamically updated by the Control Plane to include new backend clusters and their mTLS credentials.

### 2. Control Plane
The Control Plane is a Spring Boot-based management service.

- **AWS DynamoDB Backend**: Uses **AWS DynamoDB** for high-scale, reliable persistence of topic-to-cluster mappings and cluster metadata.
- **Topic Management**: Maintains a mapping of topics to their "primary" MSK clusters.
- **Cluster Management**: Keeps track of available backend MSK clusters.
- **Schema Registration**: Integrates with AWS Glue Schema Registry or other providers to ensure schemas are available across clusters.
- **Orchestration**: When a topic's primary cluster is switched, the Control Plane:
    1. Updates the routing table in the Data Plane.
    2. Provisions MirrorMaker 2 (MM2) connectors (running on MSK Connect or K8s) to replicate data from the old primary to the new primary cluster.
    3. Handles the lifecycle of these MM2 bridges.

### 3. Control Plane UI
A web-based dashboard for administrators to:
- Onboard new topics.
- Assign topics to primary clusters.
- Initiate topic migrations between clusters.
- Monitor the status of migrations.

### 4. MirrorMaker 2 (MM2)
Used for data replication during topic migration. It ensures that when a topic moves from Cluster A to Cluster B, the data is synchronized so that consumers don't lose messages.

## Data Flows

### Normal Operation (Metadata-based Routing)
1. A Kafka client connects to the **Bootstrap Virtual Cluster** endpoint.
2. The client sends a `MetadataRequest` for a specific topic.
3. The `MultiClusterRoutingFilter` allows the request to pass to a default upstream or handles it internally.
4. When the `MetadataResponse` returns, the filter:
    a. Identifies the primary cluster for each requested topic.
    b. Rewrites the `brokers` list in the response, replacing the actual backend broker addresses with the public endpoint of the corresponding **Backend Virtual Cluster**.
5. The Kafka client receives the response and connects to the returned endpoint.
6. Since the endpoint belongs to a Backend Virtual Cluster, Kroxylicious handles the specific mTLS handshake required for that backend.
7. Subsequent `Produce` or `Fetch` requests go directly through the Backend Virtual Cluster to the target Kafka cluster.

### Topic Migration Workflow
1. **Initiation**: User requests a topic migration from Cluster A to Cluster B via the UI.
2. **Bridge Establishment**: The Control Plane configures MirrorMaker 2 to start replicating the topic from A to B.
3. **Synchronization**: MM2 replicates existing data and new incoming data from A to B.
4. **Switchover**:
    - The Control Plane updates the `MultiClusterRoutingFilter` configuration.
    - New producer requests for the topic are now routed to Cluster B.
    - Consumers are also directed to Cluster B (potentially after a short overlap period or offset synchronization).
5. **Cleanup**: Once the migration is confirmed and Cluster A is no longer needed for that topic, the MM2 bridge is torn down.

## Scalability
- **Data Plane**: Kroxylicious can be scaled horizontally in Kubernetes. Load balancers distribute client connections across proxy instances.
- **Control Plane**: Can be scaled for high availability, backed by a persistent store (e.g., PostgreSQL) for the routing table.
