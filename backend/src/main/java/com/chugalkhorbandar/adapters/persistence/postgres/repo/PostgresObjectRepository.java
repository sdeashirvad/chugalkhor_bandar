package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.requireRuntime;

import com.chugalkhorbandar.adapters.persistence.PersistenceException;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ObjectJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.ObjectMapper;
import com.chugalkhorbandar.domain.world.ports.ObjectRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeObject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class PostgresObjectRepository implements ObjectRepository {

    private final ObjectJpaRepository jpa;

    public PostgresObjectRepository(ObjectJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeObject object) {
        ensureAbsent(jpa, object.id(), "Objects");
        jpa.save(ObjectMapper.toEntity(object));
    }

    @Override
    public boolean exists(String objectId) {
        return jpa.existsById(objectId);
    }

    @Override
    public Optional<RuntimeObject> findById(String objectId) {
        return jpa.findById(objectId).map(ObjectMapper::toRuntime);
    }

    @Override
    public List<RuntimeObject> findByOwner(String ownerId) {
        return jpa.findAll().stream()
                .map(ObjectMapper::toRuntime)
                .filter(object -> Objects.equals(object.ownerId(), ownerId))
                .toList();
    }

    @Override
    public void transferOwnership(String objectId, String fromOwnerId, String toOwnerId) {
        RuntimeObject object = requireRuntime(findById(objectId), objectId, "Objects");
        if (!Objects.equals(object.ownerId(), fromOwnerId)) {
            throw new PersistenceException("Object " + objectId + " is not owned by " + fromOwnerId);
        }
        jpa.save(ObjectMapper.toEntity(object.withOwnerId(toOwnerId)));
    }
}
