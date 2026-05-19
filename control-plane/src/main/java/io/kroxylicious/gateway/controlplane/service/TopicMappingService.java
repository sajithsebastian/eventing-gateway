package io.kroxylicious.gateway.controlplane.service;

import io.kroxylicious.gateway.controlplane.model.TopicMapping;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicMappingService {

    private final DynamoDbTable<TopicMapping> table;

    public TopicMappingService(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.table = enhancedClient.table("EventingGatewayTopicMappings", TableSchema.fromBean(TopicMapping.class));
    }

    public TopicMapping save(TopicMapping mapping) {
        table.putItem(mapping);
        return mapping;
    }

    public TopicMapping findById(String topicName) {
        return table.getItem(r -> r.key(k -> k.partitionValue(topicName)));
    }

    public List<TopicMapping> findAll() {
        return table.scan().items().stream().collect(Collectors.toList());
    }
}
