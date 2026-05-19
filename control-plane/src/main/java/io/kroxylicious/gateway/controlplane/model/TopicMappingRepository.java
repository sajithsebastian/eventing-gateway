package io.kroxylicious.gateway.controlplane.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicMappingRepository extends CrudRepository<TopicMapping, String> {
}
