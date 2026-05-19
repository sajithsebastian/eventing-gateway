# Eventing Gateway Solution

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
- `docs/`: Additional documentation.
