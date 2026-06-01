# Eventing Gateway Solution

## Executive Summary
The EventBus team currently manages multiple AWS MSK clusters and their associated Disaster Recovery (DR) environments. Currently, only 40% of customers utilize the proprietary EventBus Java SDK, which provides auto-discovery of MSK and DR clusters. The remaining 60% use open-source SDKs, resulting in a lack of integrated DR and control plane features.

This project introduces a centralized **Kafka Proxy (based on Kroxylicious)** to replace the need for specialized client-side SDKs. By abstracting cluster management, DR failover, and topic migrations into the data plane, the solution provides:
- **Simplified Client Integration**: Universal support for all Kafka-compatible SDKs without proprietary discovery logic.
- **Transparent DR & Migrations**: Operations such as cluster upgrades or failovers are handled at the gateway, eliminating the need for client-side configuration changes or SDK upgrades.
- **Centralized Control**: A unified control plane for topic onboarding, schema registration, and automated MirrorMaker 2 bridging.

## Overview
This project provides an eventing gateway solution based on [Kroxylicious](https://kroxylicious.io/). It enables managing multiple Kafka clusters and allows topics to be dynamically routed and migrated between them.

## Key Features
- **Kubernetes Native**: Designed to run and scale on K8s.
- **Multi-Cluster Management**: Connects to multiple backend Kafka clusters.
- **Dynamic Topic Routing**: Topics are primarily owned by one cluster, and the proxy routes traffic accordingly.
- **Topic Onboarding & Schema Registration**: Centralized control plane for topic lifecycle and schema management.
- **Automated Migration**: Automatically establishes MirrorMaker bridges when switching a topic's primary cluster.
- **Transparent Client Switching**: Producers and consumers connect to the gateway and are automatically routed to the correct backend cluster without client-side configuration changes.

## Architecture
- **Data Plane**: Kroxylicious proxy with a custom `MultiClusterRoutingFilter` filter, connecting to **AWS MSK** clusters.
- **Control Plane**: A REST API service using **AWS DynamoDB** for persistence, managing topic-to-cluster mappings and orchestrating MirrorMaker.
- **UI**: A web dashboard for topic onboarding and cluster management.
- **Schema Registry**: Integrated with the control plane for schema management.

## Project Structure
- `data-plane/`: Kroxylicious configuration and custom filters.
- `control-plane/`: API service for management.
- `ui/`: Frontend dashboard.
- `k8s/`: Kubernetes manifests and Helm charts.
- `docs/`: Detailed project documentation including:
  - [Architecture](docs/architecture.md)
  - [PRD](docs/PRD.md)
  - [Skills Matrix](docs/skills.md)
  - [Deployment Guide](docs/deployment.md)
