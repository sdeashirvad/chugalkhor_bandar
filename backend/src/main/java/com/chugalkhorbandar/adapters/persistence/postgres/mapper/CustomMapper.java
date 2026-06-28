package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.CustomEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;

public final class CustomMapper {

    private CustomMapper() {}

    public static CustomEntity toEntity(RuntimeCustom runtime) {
        CustomEntity entity = new CustomEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimeCustom toRuntime(CustomEntity entity) {
        return new RuntimeCustom(
                entity.getId(), entity.getTitle(), JsonSerialization.toMap(entity.getSections()));
    }
}
