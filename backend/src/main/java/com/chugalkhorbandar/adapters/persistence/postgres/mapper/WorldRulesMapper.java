package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.WorldRulesEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeWorldRules;

public final class WorldRulesMapper {

    private WorldRulesMapper() {}

    public static WorldRulesEntity toEntity(RuntimeWorldRules runtime) {
        WorldRulesEntity entity = new WorldRulesEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimeWorldRules toRuntime(WorldRulesEntity entity) {
        return new RuntimeWorldRules(
                entity.getId(), entity.getTitle(), JsonSerialization.toMap(entity.getSections()));
    }
}
