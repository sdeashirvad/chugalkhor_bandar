package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.RelationshipEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;

public final class RelationshipMapper {

    private RelationshipMapper() {}

    public static RelationshipEntity toEntity(RuntimeRelationship runtime) {
        RelationshipEntity entity = new RelationshipEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimeRelationship toRuntime(RelationshipEntity entity) {
        return new RuntimeRelationship(
                entity.getId(), entity.getTitle(), JsonSerialization.toMap(entity.getSections()));
    }
}
