package io.kroxylicious.gateway.controlplane.controller;

import io.kroxylicious.gateway.controlplane.model.TopicMapping;
import io.kroxylicious.gateway.controlplane.model.TopicMappingRepository;
import io.kroxylicious.gateway.controlplane.service.MigrationService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicMappingRepository repository;
    private final MigrationService migrationService;

    public TopicController(TopicMappingRepository repository, MigrationService migrationService) {
        this.repository = repository;
        this.migrationService = migrationService;
    }

    @PostMapping
    public TopicMapping onboardTopic(@RequestBody TopicMapping mapping) {
        mapping.setStatus("STABLE");
        return repository.save(mapping);
    }

    @PutMapping("/{topicName}/migrate")
    public TopicMapping migrateTopic(@PathVariable String topicName, @RequestParam String targetCluster) {
        Optional<TopicMapping> optionalMapping = repository.findById(topicName);
        if (optionalMapping.isPresent()) {
            TopicMapping mapping = optionalMapping.get();
            mapping.setStatus("MIGRATING");
            migrationService.startMigration(topicName, mapping.getPrimaryCluster(), targetCluster);
            mapping.setPrimaryCluster(targetCluster);
            return repository.save(mapping);
        }
        return null;
    }

    @GetMapping
    public Iterable<TopicMapping> listTopics() {
        return repository.findAll();
    }
}
