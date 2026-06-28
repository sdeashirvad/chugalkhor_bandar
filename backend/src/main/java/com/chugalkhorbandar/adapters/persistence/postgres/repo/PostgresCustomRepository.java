package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CustomJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.CustomMapper;
import com.chugalkhorbandar.domain.world.ports.CustomRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import java.util.List;
import java.util.Optional;

public final class PostgresCustomRepository implements CustomRepository {

    private final CustomJpaRepository jpa;

    public PostgresCustomRepository(CustomJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeCustom custom) {
        ensureAbsent(jpa, custom.id(), "Customs");
        jpa.save(CustomMapper.toEntity(custom));
    }

    @Override
    public boolean exists(String customId) {
        return jpa.existsById(customId);
    }

    @Override
    public Optional<RuntimeCustom> findById(String customId) {
        return jpa.findById(customId).map(CustomMapper::toRuntime);
    }

    @Override
    public List<RuntimeCustom> findAll() {
        return jpa.findAll().stream().map(CustomMapper::toRuntime).toList();
    }
}
