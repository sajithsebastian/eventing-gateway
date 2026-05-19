package io.kroxylicious.gateway.controlplane;

import io.kroxylicious.gateway.controlplane.model.TopicMapping;
import io.kroxylicious.gateway.controlplane.service.TopicMappingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControlPlaneApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TopicMappingService topicMappingService;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldOnboardTopic() {
        TopicMapping mapping = new TopicMapping("test-topic", "cluster-a", "schema-1", "STABLE");
        when(topicMappingService.save(any())).thenReturn(mapping);

        ResponseEntity<TopicMapping> response = restTemplate.postForEntity("/api/topics", mapping, TopicMapping.class);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test-topic", response.getBody().getTopicName());
    }
}
