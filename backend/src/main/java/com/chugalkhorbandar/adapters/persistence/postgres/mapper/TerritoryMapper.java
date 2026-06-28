package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.TerritoryEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;

public final class TerritoryMapper {

    private TerritoryMapper() {}

    public static TerritoryEntity toEntity(RuntimeTerritory runtime) {
        TerritoryEntity entity = new TerritoryEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        entity.setCurrentRulerId(runtime.currentRulerId());
        return entity;
    }

    public static RuntimeTerritory toRuntime(TerritoryEntity entity) {
        return new RuntimeTerritory(
                entity.getId(),
                entity.getTitle(),
                JsonSerialization.toMap(entity.getSections()),
                entity.getCurrentRulerId());
    }
}
