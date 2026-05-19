package io.kroxylicious.gateway.controlplane.service;

import io.kroxylicious.gateway.controlplane.model.KafkaCluster;
import io.kroxylicious.gateway.controlplane.model.TopicMapping;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfigGeneratorService {

    public String generateKroxyliciousConfig(List<KafkaCluster> clusters, List<TopicMapping> topics) {
        StringBuilder yaml = new StringBuilder();
        yaml.append("proxy:\n  address: 0.0.0.0:9092\n");
        yaml.append("virtualClusters:\n");

        // Bootstrap Router Virtual Cluster
        yaml.append("  bootstrap:\n");
        yaml.append("    targetCluster: cluster-default\n"); // Placeholder
        yaml.append("    gateways:\n      - port: 9092\n");
        yaml.append("    filters:\n");
        yaml.append("      - type: io.kroxylicious.gateway.filter.routing.MultiClusterRoutingFilterFactory\n");
        yaml.append("        config:\n");
        yaml.append("          topicToClusterMap:\n");
        for (TopicMapping topic : topics) {
            yaml.append("            ").append(topic.getTopicName()).append(": ").append(topic.getPrimaryCluster()).append("\n");
        }
        yaml.append("          clusterToEndpoints:\n");
        for (KafkaCluster cluster : clusters) {
            yaml.append("            ").append(cluster.getClusterId()).append(": ").append(cluster.getVirtualEndpoint()).append("\n");
        }

        // Backend Virtual Clusters
        for (KafkaCluster cluster : clusters) {
            yaml.append("  ").append(cluster.getClusterId()).append(":\n");
            yaml.append("    targetCluster: ").append(cluster.getClusterId()).append("-backend\n");
            yaml.append("    gateways:\n      - port: ").append(cluster.getVirtualEndpoint().split(":")[1]).append("\n");
            // mTLS Configuration would go here
            yaml.append("    tls:\n      key:\n        storeFile: /opt/kroxylicious/certs/").append(cluster.getClusterId()).append(".jks\n");
        }

        return yaml.toString();
    }
}
