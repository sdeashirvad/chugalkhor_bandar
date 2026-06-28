package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.CanonEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCanon;

public final class CanonMapper {

    private CanonMapper() {}

    public static CanonEntity toEntity(RuntimeCanon runtime) {
        CanonEntity entity = new CanonEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimeCanon toRuntime(CanonEntity entity) {
        return new RuntimeCanon(
                entity.getId(), entity.getTitle(), JsonSerialization.toMap(entity.getSections()));
    }
}
