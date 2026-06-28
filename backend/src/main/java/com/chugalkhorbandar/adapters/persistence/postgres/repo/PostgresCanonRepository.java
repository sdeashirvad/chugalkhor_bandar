package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CanonJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.CanonMapper;
import com.chugalkhorbandar.domain.world.ports.CanonRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCanon;
import java.util.List;
import java.util.Optional;

public final class PostgresCanonRepository implements CanonRepository {

    private final CanonJpaRepository jpa;

    public PostgresCanonRepository(CanonJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeCanon canon) {
        ensureAbsent(jpa, canon.id(), "Canon");
        jpa.save(CanonMapper.toEntity(canon));
    }

    @Override
    public boolean exists(String canonId) {
        return jpa.existsById(canonId);
    }

    @Override
    public Optional<RuntimeCanon> findById(String canonId) {
        return jpa.findById(canonId).map(CanonMapper::toRuntime);
    }

    @Override
    public List<RuntimeCanon> findAll() {
        return jpa.findAll().stream().map(CanonMapper::toRuntime).toList();
    }
}
