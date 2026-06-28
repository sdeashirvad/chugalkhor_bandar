package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.domain.world.ports.PromptProfileRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import java.util.List;
import java.util.Optional;

public final class InMemoryPromptProfileRepository implements PromptProfileRepository {

    private final InMemoryWorldStore store;

    public InMemoryPromptProfileRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimePromptProfile profile) {
        putUnique(store.promptProfiles(), profile.id(), profile, "PromptProfiles");
    }

    @Override
    public void update(RuntimePromptProfile profile) {
        requirePresent(store.promptProfiles(), profile.id(), "PromptProfiles");
        store.promptProfiles().put(profile.id(), profile);
    }

    @Override
    public boolean exists(String profileId) {
        return store.promptProfiles().containsKey(profileId);
    }

    @Override
    public Optional<RuntimePromptProfile> findById(String profileId) {
        return Optional.ofNullable(store.promptProfiles().get(profileId));
    }

    @Override
    public List<RuntimePromptProfile> findAll() {
        return List.copyOf(store.promptProfiles().values());
    }
}
