package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.PlaceEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;

public final class PlaceMapper {

    private PlaceMapper() {}

    public static PlaceEntity toEntity(RuntimePlace runtime) {
        PlaceEntity entity = new PlaceEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimePlace toRuntime(PlaceEntity entity) {
        return new RuntimePlace(
                entity.getId(), entity.getTitle(), JsonSerialization.toMap(entity.getSections()));
    }
}
