# Technical Skills Matrix: Eventing Gateway

## 1. Development Skills

### Backend Engineering (Java)
- **Language**: Java 21+ (utilizing modern features like Records).
- **Framework**: Spring Boot 3.x (Web, Data JPA/DynamoDB, Actuator).
- **Kafka Protocol**: Understanding of Kafka request/response schemas (Metadata, Produce, Fetch).
- **Kroxylicious API**: Developing custom filters and understanding the Filter Lifecycle.
- **AWS SDK**: Proficient in AWS SDK for Java v2 (especially DynamoDB Enhanced Client).

### Cloud & Infrastructure
- **AWS Services**: Managed Streaming for Kafka (MSK), DynamoDB, EKS, IAM.
- **Infrastructure as Code**: Terraform (VPC networking, MSK clusters, DynamoDB tables).
- **Kubernetes**: Manifest authoring, ConfigMaps, Services, Deployments, and EKS operational knowledge.

### Data Engineering
- **Kafka Ecosystem**: MirrorMaker 2 configuration, Schema Registry (AWS Glue), and Kafka Connect APIs.

## 2. Testing Skills

### Unit & Integration Testing
- **JUnit 5 / Mockito**: Writing robust tests for filter logic and API controllers.
- **Spring Boot Test**: WebLayer tests and integration testing with mocked external services.
- **Testcontainers**: (Recommended) For local testing against MSK-like environments.

### System & E2E Testing
- **Playwright**: Browser-based testing for the Control Plane UI.
- **Kafka Client Testing**: Using standard Kafka clients to verify routing and failover across the proxy.
- **Chaos Engineering**: Testing system resilience during MSK node failures or cluster migrations.

## 3. Operations & Observability
- **Monitoring**: Prometheus/Grafana for gateway metrics.
- **Logging**: Structured logging with SLF4J/Logback for audit trails of topic migrations.
- **Security**: Managing mTLS material, Java KeyStores (JKS), and IAM Roles for Service Accounts (IRSA).
