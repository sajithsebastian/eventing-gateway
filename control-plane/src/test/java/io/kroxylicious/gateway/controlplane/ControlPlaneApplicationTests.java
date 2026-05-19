package io.kroxylicious.gateway.controlplane;

import io.kroxylicious.gateway.controlplane.model.TopicMapping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControlPlaneApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldOnboardTopic() {
        TopicMapping mapping = new TopicMapping("test-topic", "cluster-a", "schema-1", "STABLE");
        ResponseEntity<TopicMapping> response = restTemplate.postForEntity("/api/topics", mapping, TopicMapping.class);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test-topic", response.getBody().getTopicName());
    }
}
