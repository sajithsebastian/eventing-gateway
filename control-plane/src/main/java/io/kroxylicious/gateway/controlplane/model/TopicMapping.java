package io.kroxylicious.gateway.controlplane.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicMapping {
    @Id
    private String topicName;
    private String primaryCluster;
    private String schemaId;
    private String status; // e.g., STABLE, MIGRATING
}
