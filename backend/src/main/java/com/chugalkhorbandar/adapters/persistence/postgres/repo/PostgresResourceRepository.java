package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.requireRuntime;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ResourceJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.ResourceMapper;
import com.chugalkhorbandar.domain.world.ports.ResourceRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeResource;
import java.util.List;
import java.util.Optional;

public final class PostgresResourceRepository implements ResourceRepository {

    private final ResourceJpaRepository jpa;

    public PostgresResourceRepository(ResourceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeResource resource) {
        ensureAbsent(jpa, resource.id(), "Resources");
        jpa.save(ResourceMapper.toEntity(resource));
    }

    @Override
    public boolean exists(String resourceId) {
        return jpa.existsById(resourceId);
    }

    @Override
    public Optional<RuntimeResource> findById(String resourceId) {
        return jpa.findById(resourceId).map(ResourceMapper::toRuntime);
    }

    @Override
    public List<RuntimeResource> findAll() {
        return jpa.findAll().stream().map(ResourceMapper::toRuntime).toList();
    }

    @Override
    public void consume(String resourceId, String consumerId, int quantity) {
        RuntimeResource resource = requireRuntime(findById(resourceId), resourceId, "Resources");
        int remaining = resource.availableQuantity() - quantity;
        jpa.save(ResourceMapper.toEntity(resource.withAvailableQuantity(Math.max(0, remaining))));
    }
}
