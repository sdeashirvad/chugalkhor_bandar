package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.adapters.persistence.PersistenceException;
import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class InMemoryCharacterRepository implements CharacterRepository {

    private final InMemoryWorldStore store;

    public InMemoryCharacterRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeCharacter character) {
        putUnique(store.characters(), character.id(), character, "Characters");
    }

    @Override
    public void update(RuntimeCharacter character) {
        requirePresent(store.characters(), character.id(), "Characters");
        store.characters().put(character.id(), character);
    }

    @Override
    public void delete(String characterId) {
        requirePresent(store.characters(), characterId, "Characters");
        store.characters().remove(characterId);
    }

    @Override
    public boolean exists(String characterId) {
        return store.characters().containsKey(characterId);
    }

    @Override
    public Optional<RuntimeCharacter> findById(String characterId) {
        return Optional.ofNullable(store.characters().get(characterId));
    }

    @Override
    public List<RuntimeCharacter> findAll(CharacterQuery query) {
        return store.characters().values().stream()
                .filter(character -> matchesPlace(character, query.placeId()))
                .filter(character -> matchesTitle(character, query.titleContains()))
                .toList();
    }

    @Override
    public void moveCharacter(String characterId, String fromPlaceId, String toPlaceId) {
        RuntimeCharacter character = requirePresent(store.characters(), characterId, "Characters");
        if (!Objects.equals(character.currentPlaceId(), fromPlaceId)) {
            throw new PersistenceException(
                    "Character " + characterId + " is not at place " + fromPlaceId);
        }
        store.characters().put(characterId, character.withCurrentPlaceId(toPlaceId));
    }

    @Override
    public void changePreference(String characterId, String preferenceKey, String preferenceValue) {
        RuntimeCharacter character = requirePresent(store.characters(), characterId, "Characters");
        store.characters().put(characterId, character.withPreference(preferenceKey, preferenceValue));
    }

    @Override
    public void assignTitle(String characterId, String title) {
        RuntimeCharacter character = requirePresent(store.characters(), characterId, "Characters");
        store.characters().put(characterId, character.withTitle(title));
    }

    @Override
    public void transferOwnership(String characterId, String newOwnerId) {
        RuntimeCharacter character = requirePresent(store.characters(), characterId, "Characters");
        Map<String, String> sections = new LinkedHashMap<>(character.sections());
        sections.put("owner", newOwnerId);
        store.characters().put(characterId, character.withSections(Map.copyOf(sections)));
    }

    private static boolean matchesPlace(RuntimeCharacter character, String placeId) {
        return placeId == null || placeId.equals(character.currentPlaceId());
    }

    private static boolean matchesTitle(RuntimeCharacter character, String titleContains) {
        return titleContains == null
                || character.title().toLowerCase().contains(titleContains.toLowerCase());
    }
}
