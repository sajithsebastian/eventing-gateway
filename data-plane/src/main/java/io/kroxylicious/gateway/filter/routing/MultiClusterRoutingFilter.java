package io.kroxylicious.gateway.filter.routing;

import io.kroxylicious.proxy.filter.RequestFilterResult;
import io.kroxylicious.proxy.filter.FilterContext;
import io.kroxylicious.proxy.filter.RequestFilter;
import org.apache.kafka.common.message.RequestHeaderData;
import org.apache.kafka.common.message.MetadataRequestData;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.ApiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * A filter that routes Kafka requests to different backend clusters based on the topic.
 */
public class MultiClusterRoutingFilter implements RequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiClusterRoutingFilter.class);

    private final Map<String, String> topicToClusterMap;

    public MultiClusterRoutingFilter(Map<String, String> topicToClusterMap) {
        this.topicToClusterMap = topicToClusterMap;
    }

    @Override
    public CompletionStage<RequestFilterResult> onRequest(ApiKeys apiKey, RequestHeaderData header, ApiMessage request, FilterContext context) {
        if (apiKey == ApiKeys.METADATA) {
            MetadataRequestData metadataRequest = (MetadataRequestData) request;
            if (metadataRequest.topics() != null && !metadataRequest.topics().isEmpty()) {
                String topicName = metadataRequest.topics().iterator().next().name();
                String targetCluster = topicToClusterMap.get(topicName);
                if (targetCluster != null) {
                    LOGGER.info("Routing topic {} to cluster {}", topicName, targetCluster);
                    // In a real implementation with Kroxylicious, we would use context to select the upstream cluster.
                    // For this scaffold, we demonstrate the interception point.
                }
            }
        }
        return context.forwardRequest(header, request);
    }
}
