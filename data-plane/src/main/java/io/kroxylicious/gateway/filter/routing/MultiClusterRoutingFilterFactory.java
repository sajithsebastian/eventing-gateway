package io.kroxylicious.gateway.filter.routing;

import io.kroxylicious.proxy.filter.FilterFactory;
import io.kroxylicious.proxy.filter.FilterFactoryContext;
import io.kroxylicious.proxy.filter.Filter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class MultiClusterRoutingFilterFactory implements FilterFactory<MultiClusterRoutingFilterFactory.Config, MultiClusterRoutingFilterFactory.Config> {

    @Override
    public Config initialize(FilterFactoryContext context, Config config) {
        return config;
    }

    @Override
    public Filter createFilter(FilterFactoryContext context, Config config) {
        return new MultiClusterRoutingFilter(config.topicToClusterMap(), config.clusterToEndpoints());
    }

    public record Config(
        @JsonProperty(required = true) Map<String, String> topicToClusterMap,
        @JsonProperty(required = true) Map<String, String> clusterToEndpoints
    ) {}
}
