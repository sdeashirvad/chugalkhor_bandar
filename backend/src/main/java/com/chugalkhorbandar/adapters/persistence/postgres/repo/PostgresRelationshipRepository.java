package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensurePresent;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.RelationshipJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.RelationshipMapper;
import com.chugalkhorbandar.domain.world.ports.RelationshipRepository;
import com.chugalkhorbandar.domain.world.ports.query.RelationshipQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class PostgresRelationshipRepository implements RelationshipRepository {

    private final RelationshipJpaRepository jpa;

    public PostgresRelationshipRepository(RelationshipJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeRelationship relationship) {
        ensureAbsent(jpa, relationship.id(), "Relationships");
        jpa.save(RelationshipMapper.toEntity(relationship));
    }

    @Override
    public void remove(String relationshipId) {
        ensurePresent(jpa, relationshipId, "Relationships");
        jpa.deleteById(relationshipId);
    }

    @Override
    public boolean exists(String relationshipId) {
        return jpa.existsById(relationshipId);
    }

    @Override
    public Optional<RuntimeRelationship> findById(String relationshipId) {
        return jpa.findById(relationshipId).map(RelationshipMapper::toRuntime);
    }

    @Override
    public List<RuntimeRelationship> findBetween(String firstCharacterId, String secondCharacterId) {
        return jpa.findAll().stream()
                .map(RelationshipMapper::toRuntime)
                .filter(relationship -> involvesBoth(relationship, firstCharacterId, secondCharacterId))
                .toList();
    }

    @Override
    public List<RuntimeRelationship> findByCharacter(String characterId) {
        return jpa.findAll().stream()
                .map(RelationshipMapper::toRuntime)
                .filter(relationship -> involvesCharacter(relationship, characterId))
                .toList();
    }

    @Override
    public List<RuntimeRelationship> findAll(RelationshipQuery query) {
        return jpa.findAll().stream()
                .map(RelationshipMapper::toRuntime)
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
