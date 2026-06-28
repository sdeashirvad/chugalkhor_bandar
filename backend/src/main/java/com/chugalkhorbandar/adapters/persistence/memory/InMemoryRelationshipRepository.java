package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.domain.world.ports.RelationshipRepository;
import com.chugalkhorbandar.domain.world.ports.query.RelationshipQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class InMemoryRelationshipRepository implements RelationshipRepository {

    private final InMemoryWorldStore store;

    public InMemoryRelationshipRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeRelationship relationship) {
        putUnique(store.relationships(), relationship.id(), relationship, "Relationships");
    }

    @Override
    public void remove(String relationshipId) {
        requirePresent(store.relationships(), relationshipId, "Relationships");
        store.relationships().remove(relationshipId);
    }

    @Override
    public boolean exists(String relationshipId) {
        return store.relationships().containsKey(relationshipId);
    }

    @Override
    public Optional<RuntimeRelationship> findById(String relationshipId) {
        return Optional.ofNullable(store.relationships().get(relationshipId));
    }

    @Override
    public List<RuntimeRelationship> findBetween(String firstCharacterId, String secondCharacterId) {
        return store.relationships().values().stream()
                .filter(relationship -> involvesBoth(relationship, firstCharacterId, secondCharacterId))
                .toList();
    }

    @Override
    public List<RuntimeRelationship> findByCharacter(String characterId) {
        return store.relationships().values().stream()
                .filter(relationship -> involvesCharacter(relationship, characterId))
                .toList();
    }

    @Override
    public List<RuntimeRelationship> findAll(RelationshipQuery query) {
        return store.relationships().values().stream()
                .filter(relationship -> matchesCharacter(relationship, query.characterId()))
                .filter(relationship -> matchesType(relationship, query.relationshipType()))
                .toList();
    }

    private static boolean involvesBoth(
            RuntimeRelationship relationship, String firstCharacterId, String secondCharacterId) {
        return involvesCharacter(relationship, firstCharacterId)
                && involvesCharacter(relationship, secondCharacterId);
    }

    private static boolean involvesCharacter(RuntimeRelationship relationship, String characterId) {
        return relationship.sections().values().stream().anyMatch(value -> value.contains(characterId));
    }

    private static boolean matchesCharacter(RuntimeRelationship relationship, String characterId) {
        return characterId == null || involvesCharacter(relationship, characterId);
    }

    private static boolean matchesType(RuntimeRelationship relationship, String relationshipType) {
        if (relationshipType == null) {
            return true;
        }
        return Objects.equals(relationship.sections().get("relationshipType"), relationshipType);
    }
}
