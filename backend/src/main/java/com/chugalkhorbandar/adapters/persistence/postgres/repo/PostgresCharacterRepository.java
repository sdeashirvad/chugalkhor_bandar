package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensurePresent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.requireRuntime;

import com.chugalkhorbandar.adapters.persistence.PersistenceException;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CharacterJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.CharacterMapper;
import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PostgresCharacterRepository implements CharacterRepository {

    private final CharacterJpaRepository jpa;

    public PostgresCharacterRepository(CharacterJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeCharacter character) {
        ensureAbsent(jpa, character.id(), "Characters");
        jpa.save(CharacterMapper.toEntity(character));
    }

    @Override
    public void update(RuntimeCharacter character) {
        ensurePresent(jpa, character.id(), "Characters");
        jpa.save(CharacterMapper.toEntity(character));
    }

    @Override
    public void delete(String characterId) {
        ensurePresent(jpa, characterId, "Characters");
        jpa.deleteById(characterId);
    }

    @Override
    public boolean exists(String characterId) {
        return jpa.existsById(characterId);
    }

    @Override
    public Optional<RuntimeCharacter> findById(String characterId) {
        return jpa.findById(characterId).map(CharacterMapper::toRuntime);
    }

    @Override
    public List<RuntimeCharacter> findAll(CharacterQuery query) {
        return jpa.findAll().stream()
                .map(CharacterMapper::toRuntime)
                .filter(character -> matchesPlace(character, query.placeId()))
                .filter(character -> matchesTitle(character, query.titleContains()))
                .toList();
    }

    @Override
    public void moveCharacter(String characterId, String fromPlaceId, String toPlaceId) {
        RuntimeCharacter character = requireRuntime(findById(characterId), characterId, "Characters");
        if (!Objects.equals(character.currentPlaceId(), fromPlaceId)) {
            throw new PersistenceException(
                    "Character " + characterId + " is not at place " + fromPlaceId);
        }
        jpa.save(CharacterMapper.toEntity(character.withCurrentPlaceId(toPlaceId)));
    }

    @Override
    public void changePreference(String characterId, String preferenceKey, String preferenceValue) {
        RuntimeCharacter character = requireRuntime(findById(characterId), characterId, "Characters");
        jpa.save(CharacterMapper.toEntity(character.withPreference(preferenceKey, preferenceValue)));
    }

    @Override
    public void assignTitle(String characterId, String title) {
        RuntimeCharacter character = requireRuntime(findById(characterId), characterId, "Characters");
        jpa.save(CharacterMapper.toEntity(character.withTitle(title)));
    }

    @Override
    public void transferOwnership(String characterId, String newOwnerId) {
        RuntimeCharacter character = requireRuntime(findById(characterId), characterId, "Characters");
        Map<String, String> sections = new LinkedHashMap<>(character.sections());
        sections.put("owner", newOwnerId);
        jpa.save(CharacterMapper.toEntity(character.withSections(Map.copyOf(sections))));
    }

    private static boolean matchesPlace(RuntimeCharacter character, String placeId) {
        return placeId == null || placeId.equals(character.currentPlaceId());
    }

    private static boolean matchesTitle(RuntimeCharacter character, String titleContains) {
        return titleContains == null
                || character.title().toLowerCase().contains(titleContains.toLowerCase());
    }
}
