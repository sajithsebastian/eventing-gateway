package io.kroxylicious.gateway.controlplane.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MigrationService {
    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

    public void startMigration(String topic, String sourceCluster, String targetCluster) {
        logger.info("Starting migration for topic {} from {} to {}", topic, sourceCluster, targetCluster);
        // Logic to interface with Kafka Connect / MirrorMaker 2 API
        establishMirrorMakerBridge(topic, sourceCluster, targetCluster);
    }

    private void establishMirrorMakerBridge(String topic, String source, String target) {
        logger.info("Provisioning MirrorMaker 2 connector for topic {}", topic);
        // Real implementation would POST to Kafka Connect
        String mm2Config = generateMM2Config(topic, source, target);
        logger.info("Generated MM2 Config: \n{}", mm2Config);
    }

    private String generateMM2Config(String topic, String source, String target) {
        return String.format("""
            clusters = %s, %s
            %s.bootstrap.servers = %s-bootstrap:9092
            %s.bootstrap.servers = %s-bootstrap:9092

            %s->%s.enabled = true
            %s->%s.topics = %s
            """, source, target, source, source, target, target, source, target, source, target, topic);
    }
}
