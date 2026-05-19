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
public class KafkaCluster {
    private String clusterId;
    private String bootstrapServers;
    private String truststoreBase64;
    private String keystoreBase64;
    private String virtualEndpoint;

    @DynamoDbPartitionKey
    public String getClusterId() {
        return clusterId;
    }
}
