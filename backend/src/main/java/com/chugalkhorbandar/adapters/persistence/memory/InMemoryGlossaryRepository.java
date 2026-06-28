package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;

import com.chugalkhorbandar.domain.world.ports.GlossaryRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeGlossaryEntry;
import java.util.List;
import java.util.Optional;

public final class InMemoryGlossaryRepository implements GlossaryRepository {

    private final InMemoryWorldStore store;

    public InMemoryGlossaryRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeGlossaryEntry entry) {
        putUnique(store.glossary(), entry.id(), entry, "Glossary");
    }

    @Override
    public boolean exists(String glossaryId) {
        return store.glossary().containsKey(glossaryId);
    }

    @Override
    public Optional<RuntimeGlossaryEntry> findById(String glossaryId) {
        return Optional.ofNullable(store.glossary().get(glossaryId));
    }

    @Override
    public List<RuntimeGlossaryEntry> findAll() {
        return List.copyOf(store.glossary().values());
    }
}
