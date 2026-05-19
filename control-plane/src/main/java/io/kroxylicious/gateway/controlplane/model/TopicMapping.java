package io.kroxylicious.gateway.controlplane.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class TopicMapping {
    private String topicName;
    private String primaryCluster;
    private String schemaId;
    private String status; // e.g., STABLE, MIGRATING

    @DynamoDbPartitionKey
    public String getTopicName() {
        return topicName;
    }
}
