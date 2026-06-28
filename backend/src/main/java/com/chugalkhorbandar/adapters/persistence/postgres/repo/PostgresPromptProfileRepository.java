package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensurePresent;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.PromptProfileJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.PromptProfileMapper;
import com.chugalkhorbandar.domain.world.ports.PromptProfileRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import java.util.List;
import java.util.Optional;

public final class PostgresPromptProfileRepository implements PromptProfileRepository {

    private final PromptProfileJpaRepository jpa;

    public PostgresPromptProfileRepository(PromptProfileJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimePromptProfile profile) {
        ensureAbsent(jpa, profile.id(), "PromptProfiles");
        jpa.save(PromptProfileMapper.toEntity(profile));
    }

    @Override
    public void update(RuntimePromptProfile profile) {
        ensurePresent(jpa, profile.id(), "PromptProfiles");
        jpa.save(PromptProfileMapper.toEntity(profile));
    }

    @Override
    public boolean exists(String profileId) {
        return jpa.existsById(profileId);
    }

    @Override
    public Optional<RuntimePromptProfile> findById(String profileId) {
        return jpa.findById(profileId).map(PromptProfileMapper::toRuntime);
    }

    @Override
    public List<RuntimePromptProfile> findAll() {
        return jpa.findAll().stream().map(PromptProfileMapper::toRuntime).toList();
    }
}
