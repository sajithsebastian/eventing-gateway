package io.kroxylicious.gateway.filter.routing;

import io.kroxylicious.proxy.filter.FilterContext;
import io.kroxylicious.proxy.filter.RequestFilter;
import io.kroxylicious.proxy.filter.ResponseFilter;
import io.kroxylicious.proxy.filter.RequestFilterResult;
import io.kroxylicious.proxy.filter.ResponseFilterResult;
import org.apache.kafka.common.message.MetadataResponseData;
import org.apache.kafka.common.message.RequestHeaderData;
import org.apache.kafka.common.message.ResponseHeaderData;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.CompletionStage;

/**
 * A filter that routes Kafka clients to different backend virtual clusters by rewriting Metadata responses.
 * Note: This implementation currently handles Metadata requests for topics that all reside on the same backend cluster.
 * Multi-topic requests spanning multiple clusters will result in an error to avoid ambiguous routing.
 */
public class MultiClusterRoutingFilter implements RequestFilter, ResponseFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiClusterRoutingFilter.class);

    private final Map<String, String> topicToClusterMap;
    private final Map<String, String> clusterToEndpoints;

    public MultiClusterRoutingFilter(Map<String, String> topicToClusterMap, Map<String, String> clusterToEndpoints) {
        this.topicToClusterMap = topicToClusterMap;
        this.clusterToEndpoints = clusterToEndpoints;
    }

    @Override
    public CompletionStage<RequestFilterResult> onRequest(ApiKeys apiKey, RequestHeaderData header, ApiMessage request, FilterContext context) {
        return context.forwardRequest(header, request);
    }

    @Override
    public CompletionStage<ResponseFilterResult> onResponse(ApiKeys apiKey, ResponseHeaderData header, ApiMessage response, FilterContext context) {
        if (apiKey == ApiKeys.METADATA) {
            MetadataResponseData metadataResponse = (MetadataResponseData) response;
            rewriteMetadataResponse(metadataResponse);
        }
        return context.forwardResponse(header, response);
    }

    private void rewriteMetadataResponse(MetadataResponseData data) {
        Set<String> targetClusters = new HashSet<>();
        for (MetadataResponseData.MetadataResponseTopic topic : data.topics()) {
            String clusterName = topicToClusterMap.get(topic.name());
            if (clusterName != null) {
                targetClusters.add(clusterName);
            }
        }

        if (targetClusters.size() > 1) {
            LOGGER.error("Metadata request spans multiple clusters: {}. Failing request to prevent ambiguous routing.", targetClusters);
            // In a real proxy, we might return a response with TOPIC_AUTHORIZATION_FAILED or similar error for all topics.
            for (MetadataResponseData.MetadataResponseTopic topic : data.topics()) {
                topic.setErrorCode(Errors.INVALID_TOPIC_EXCEPTION.code());
            }
        } else if (targetClusters.size() == 1) {
            String clusterName = targetClusters.iterator().next();
            String virtualEndpoint = clusterToEndpoints.get(clusterName);
            if (virtualEndpoint != null) {
                LOGGER.info("Rewriting metadata for topics to use cluster {} at {}", clusterName, virtualEndpoint);
                updateBrokerEndpoints(data, virtualEndpoint);
            }
        }
    }

    private void updateBrokerEndpoints(MetadataResponseData data, String endpoint) {
        String[] parts = endpoint.split(":");
        String host = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9092;

        for (MetadataResponseData.MetadataResponseBroker broker : data.brokers()) {
            broker.setHost(host);
            broker.setPort(port);
        }
    }
}
