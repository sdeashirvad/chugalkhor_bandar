package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.PromptProfileEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;

public final class PromptProfileMapper {

    private PromptProfileMapper() {}

    public static PromptProfileEntity toEntity(RuntimePromptProfile runtime) {
        PromptProfileEntity entity = new PromptProfileEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimePromptProfile toRuntime(PromptProfileEntity entity) {
        return new RuntimePromptProfile(
                entity.getId(), entity.getTitle(), JsonSerialization.toMap(entity.getSections()));
    }
}
