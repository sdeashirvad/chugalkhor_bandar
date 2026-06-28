package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.ObjectEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeObject;

public final class ObjectMapper {

    private ObjectMapper() {}

    public static ObjectEntity toEntity(RuntimeObject runtime) {
        ObjectEntity entity = new ObjectEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        entity.setOwnerId(runtime.ownerId());
        return entity;
    }

    public static RuntimeObject toRuntime(ObjectEntity entity) {
        return new RuntimeObject(
                entity.getId(),
                entity.getTitle(),
                JsonSerialization.toMap(entity.getSections()),
                entity.getOwnerId());
    }
}
