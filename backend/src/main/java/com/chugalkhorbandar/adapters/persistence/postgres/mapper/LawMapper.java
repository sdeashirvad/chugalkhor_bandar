package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.LawEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeLaw;

public final class LawMapper {

    private LawMapper() {}

    public static LawEntity toEntity(RuntimeLaw runtime) {
        LawEntity entity = new LawEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimeLaw toRuntime(LawEntity entity) {
        return new RuntimeLaw(
                entity.getId(), entity.getTitle(), JsonSerialization.toMap(entity.getSections()));
    }
}
