package io.kroxylicious.gateway.filter.routing;

import io.kroxylicious.proxy.filter.FilterContext;
import io.kroxylicious.proxy.filter.RequestFilterResult;
import org.apache.kafka.common.message.RequestHeaderData;
import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.ApiMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MultiClusterRoutingFilterTest {

    @Test
    void shouldForwardRequest() {
        Map<String, String> config = Map.of("topic1", "clusterA");
        MultiClusterRoutingFilter filter = new MultiClusterRoutingFilter(config);

        FilterContext context = Mockito.mock(FilterContext.class);
        RequestHeaderData header = new RequestHeaderData();
        ApiMessage request = Mockito.mock(ApiMessage.class);
        RequestFilterResult result = Mockito.mock(RequestFilterResult.class);

        when(context.forwardRequest(any(), any())).thenReturn(CompletableFuture.completedStage(result));

        CompletionStage<RequestFilterResult> stage = filter.onRequest(ApiKeys.PRODUCE, header, request, context);

        assertNotNull(stage);
        verify(context).forwardRequest(header, request);
    }
}
