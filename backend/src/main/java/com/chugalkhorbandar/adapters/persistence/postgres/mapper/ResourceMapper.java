package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.ResourceEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeResource;

public final class ResourceMapper {

    private ResourceMapper() {}

    public static ResourceEntity toEntity(RuntimeResource runtime) {
        ResourceEntity entity = new ResourceEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        entity.setAvailableQuantity(runtime.availableQuantity());
        return entity;
    }

    public static RuntimeResource toRuntime(ResourceEntity entity) {
        return new RuntimeResource(
                entity.getId(),
                entity.getTitle(),
                JsonSerialization.toMap(entity.getSections()),
                entity.getAvailableQuantity());
    }
}
