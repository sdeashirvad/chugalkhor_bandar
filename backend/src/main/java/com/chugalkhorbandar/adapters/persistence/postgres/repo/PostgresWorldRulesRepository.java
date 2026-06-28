package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensurePresent;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.WorldRulesJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.WorldRulesMapper;
import com.chugalkhorbandar.domain.world.ports.WorldRulesRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeWorldRules;
import java.util.List;
import java.util.Optional;

public final class PostgresWorldRulesRepository implements WorldRulesRepository {

    private final WorldRulesJpaRepository jpa;

    public PostgresWorldRulesRepository(WorldRulesJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeWorldRules worldRules) {
        ensureAbsent(jpa, worldRules.id(), "WorldRules");
        jpa.save(WorldRulesMapper.toEntity(worldRules));
    }

    @Override
    public void update(RuntimeWorldRules worldRules) {
        ensurePresent(jpa, worldRules.id(), "WorldRules");
        jpa.save(WorldRulesMapper.toEntity(worldRules));
    }

    @Override
    public boolean exists(String worldRulesId) {
        return jpa.existsById(worldRulesId);
    }

    @Override
    public Optional<RuntimeWorldRules> findById(String worldRulesId) {
        return jpa.findById(worldRulesId).map(WorldRulesMapper::toRuntime);
    }

    @Override
    public List<RuntimeWorldRules> findAll() {
        return jpa.findAll().stream().map(WorldRulesMapper::toRuntime).toList();
    }
}
