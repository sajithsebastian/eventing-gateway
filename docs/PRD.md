# Product Requirements Document (PRD): Eventing Gateway

## 1. Vision
To provide a unified, protocol-aware Kafka gateway that abstracts the underlying infrastructure (MSK, DR clusters) from clients. This enables seamless topic migrations, disaster recovery, and centralized management without requiring client-side SDK upgrades or configuration changes.

## 2. Problem Statement
The current EventBus platform relies on a proprietary Java SDK for topic discovery and DR handling. This has several drawbacks:
- **Fragmentation**: Only 40% of clients use the Java SDK; 60% are on open-source SDKs without DR/Control Plane integration.
- **Maintenance Overhead**: Every DR enablement or cluster migration requires client-side configuration updates or SDK upgrades.
- **Complexity**: Clients are burdened with infrastructure-level logic that should be centralized.

## 3. Goals
- **Infrastructure Abstraction**: Clients connect to a single gateway endpoint.
- **Universal Compatibility**: Support 100% of Kafka-compatible SDKs.
- **Transparent Operations**: Handle DR failover and migrations in the data plane.
- **Centralized Orchestration**: Automated MirrorMaker 2 bridging and schema management.
- **Scalability & Resilience**: Cloud-native (K8s) solution using managed AWS services (MSK, DynamoDB).

## 4. Key Features

### 4.1 Data Plane (Kroxylicious)
- **Bootstrap Router**: Multi-cluster routing using Metadata rewriting.
- **Per-Backend mTLS**: Dedicated virtual clusters for backend-specific security identities.
- **Protocol Awareness**: Deep inspection of Kafka messages to identify topic-to-cluster mappings.

### 4.2 Control Plane (Spring Boot)
- **Topic Management API**: REST endpoints for topic onboarding and primary cluster assignment.
- **Automated Migration**: One-click migration with background MirrorMaker 2 orchestration.
- **DynamoDB Persistence**: Highly available, stateless storage for routing tables.
- **Config Generation**: Dynamic generation of Kroxylicious YAML based on active clusters and topics.

### 4.3 Management UI
- **Dashboard**: Visualize topic distribution across MSK clusters.
- **Onboarding Wizard**: Register new topics and assign schemas.
- **Migration Interface**: Initiate and monitor topic switchovers.

## 5. Technical Requirements
- **Cloud Provider**: AWS (MSK, DynamoDB, EKS).
- **IaC**: Terraform for infrastructure provisioning.
- **Runtime**: Kubernetes (K8s).
- **Language**: Java 21+ for proxy filters and control plane.

## 6. Roadmap
- **Phase 1 (MVP)**: Basic multi-cluster routing and manual migration via API.
- **Phase 2**: UI integration and automated MM2 bridging.
- **Phase 3**: AWS Glue Schema Registry integration and full mTLS automation.
- **Phase 4**: Advanced observability (OpenTelemetry) and automated DR failover detection.
