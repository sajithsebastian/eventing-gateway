# Eventing Gateway Architecture

## Components

### 1. Data Plane (Kroxylicious Proxy)
The data plane is powered by **Kroxylicious**, a Kafka protocol-aware proxy. It serves as the single entry point for all Kafka clients (producers and consumers).

- **AWS MSK Integration**: The gateway connects to multiple **AWS MSK (Managed Streaming for Kafka)** clusters as backend clusters.
- **MultiClusterRoutingFilter**: A custom filter that inspects incoming Kafka requests. It identifies the topic being accessed and routes the request to the appropriate backend MSK cluster based on a dynamic routing table.
- **Dynamic Configuration**: The proxy receives its routing table from the Control Plane.

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

### Normal Operation
1. A Kafka client connects to the Kroxylicious Gateway.
2. The client sends a `MetadataRequest` to find the leader for a topic.
3. The `MultiClusterRoutingFilter` intercepts the request, looks up the topic's primary cluster, and proxies the request to that cluster.
4. Subsequent `Produce` or `Fetch` requests are similarly routed to the correct backend cluster.

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
