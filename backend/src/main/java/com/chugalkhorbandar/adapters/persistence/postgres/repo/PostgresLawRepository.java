package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.LawJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.LawMapper;
import com.chugalkhorbandar.domain.world.ports.LawRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeLaw;
import java.util.List;
import java.util.Optional;

public final class PostgresLawRepository implements LawRepository {

    private final LawJpaRepository jpa;

    public PostgresLawRepository(LawJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeLaw law) {
        ensureAbsent(jpa, law.id(), "Laws");
        jpa.save(LawMapper.toEntity(law));
    }

    @Override
    public boolean exists(String lawId) {
        return jpa.existsById(lawId);
    }

    @Override
    public Optional<RuntimeLaw> findById(String lawId) {
        return jpa.findById(lawId).map(LawMapper::toRuntime);
    }

    @Override
    public List<RuntimeLaw> findAll() {
        return jpa.findAll().stream().map(LawMapper::toRuntime).toList();
    }
}
