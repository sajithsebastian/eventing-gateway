package io.kroxylicious.gateway.controlplane.controller;

import io.kroxylicious.gateway.controlplane.model.TopicMapping;
import io.kroxylicious.gateway.controlplane.service.TopicMappingService;
import io.kroxylicious.gateway.controlplane.service.MigrationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicMappingService topicMappingService;
    private final MigrationService migrationService;

    public TopicController(TopicMappingService topicMappingService, MigrationService migrationService) {
        this.topicMappingService = topicMappingService;
        this.migrationService = migrationService;
    }

    @PostMapping
    public TopicMapping onboardTopic(@RequestBody TopicMapping mapping) {
        mapping.setStatus("STABLE");
        return topicMappingService.save(mapping);
    }

    @PutMapping("/{topicName}/migrate")
    public TopicMapping migrateTopic(@PathVariable String topicName, @RequestParam String targetCluster) {
        TopicMapping mapping = topicMappingService.findById(topicName);
        if (mapping != null) {
            mapping.setStatus("MIGRATING");
            migrationService.startMigration(topicName, mapping.getPrimaryCluster(), targetCluster);
            mapping.setPrimaryCluster(targetCluster);
            return topicMappingService.save(mapping);
        }
        return null;
    }

    @GetMapping
    public List<TopicMapping> listTopics() {
        return topicMappingService.findAll();
    }
}
