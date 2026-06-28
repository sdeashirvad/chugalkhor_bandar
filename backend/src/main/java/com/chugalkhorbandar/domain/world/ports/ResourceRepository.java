package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeResource;
import java.util.List;
import java.util.Optional;

public interface ResourceRepository {

    void create(RuntimeResource resource);

    boolean exists(String resourceId);

    Optional<RuntimeResource> findById(String resourceId);

    List<RuntimeResource> findAll();

    void consume(String resourceId, String consumerId, int quantity);
}
