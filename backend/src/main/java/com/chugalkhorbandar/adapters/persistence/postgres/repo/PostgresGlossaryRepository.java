package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.GlossaryEntryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.GlossaryEntryMapper;
import com.chugalkhorbandar.domain.world.ports.GlossaryRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeGlossaryEntry;
import java.util.List;
import java.util.Optional;

public final class PostgresGlossaryRepository implements GlossaryRepository {

    private final GlossaryEntryJpaRepository jpa;

    public PostgresGlossaryRepository(GlossaryEntryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeGlossaryEntry entry) {
        ensureAbsent(jpa, entry.id(), "Glossary");
        jpa.save(GlossaryEntryMapper.toEntity(entry));
    }

    @Override
    public boolean exists(String glossaryId) {
        return jpa.existsById(glossaryId);
    }

    @Override
    public Optional<RuntimeGlossaryEntry> findById(String glossaryId) {
        return jpa.findById(glossaryId).map(GlossaryEntryMapper::toRuntime);
    }

    @Override
    public List<RuntimeGlossaryEntry> findAll() {
        return jpa.findAll().stream().map(GlossaryEntryMapper::toRuntime).toList();
    }
}
