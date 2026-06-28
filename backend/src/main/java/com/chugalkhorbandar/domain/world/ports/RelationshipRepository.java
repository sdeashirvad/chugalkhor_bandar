package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.ports.query.RelationshipQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import java.util.List;
import java.util.Optional;

public interface RelationshipRepository {

    void create(RuntimeRelationship relationship);

    void remove(String relationshipId);

    boolean exists(String relationshipId);

    Optional<RuntimeRelationship> findById(String relationshipId);

    List<RuntimeRelationship> findBetween(String firstCharacterId, String secondCharacterId);

    List<RuntimeRelationship> findByCharacter(String characterId);

    List<RuntimeRelationship> findAll(RelationshipQuery query);
}
