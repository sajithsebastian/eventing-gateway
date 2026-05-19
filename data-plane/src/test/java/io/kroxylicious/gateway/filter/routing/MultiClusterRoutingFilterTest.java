package io.kroxylicious.gateway.filter.routing;

import io.kroxylicious.proxy.filter.FilterContext;
import io.kroxylicious.proxy.filter.ResponseFilterResult;
import org.apache.kafka.common.message.MetadataResponseData;
import org.apache.kafka.common.message.ResponseHeaderData;
import org.apache.kafka.common.protocol.ApiKeys;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MultiClusterRoutingFilterTest {

    @Test
    void shouldRewriteMetadataResponse() {
        Map<String, String> topicMap = Map.of("topic1", "clusterA");
        Map<String, String> endpointMap = Map.of("clusterA", "virtual-a:9093");
        MultiClusterRoutingFilter filter = new MultiClusterRoutingFilter(topicMap, endpointMap);

        FilterContext context = Mockito.mock(FilterContext.class);
        ResponseHeaderData header = new ResponseHeaderData();
        MetadataResponseData response = new MetadataResponseData();

        // Add a broker
        response.brokers().add(new MetadataResponseData.MetadataResponseBroker().setNodeId(1).setHost("real-host").setPort(9092));
        // Add a topic
        response.topics().add(new MetadataResponseData.MetadataResponseTopic().setName("topic1"));

        ResponseFilterResult result = Mockito.mock(ResponseFilterResult.class);
        when(context.forwardResponse(any(), any())).thenReturn(CompletableFuture.completedStage(result));

        CompletionStage<ResponseFilterResult> stage = filter.onResponse(ApiKeys.METADATA, header, response, context);

        assertNotNull(stage);
        // Verify broker was rewritten
        assertEquals("virtual-a", response.brokers().iterator().next().host());
        assertEquals(9093, response.brokers().iterator().next().port());
    }

    private void assertNotNull(Object obj) {
        if (obj == null) throw new AssertionError("Object is null");
    }
}
