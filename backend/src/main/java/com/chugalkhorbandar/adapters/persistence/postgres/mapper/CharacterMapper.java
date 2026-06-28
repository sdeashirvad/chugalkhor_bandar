package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.CharacterEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;

public final class CharacterMapper {

    private CharacterMapper() {}

    public static CharacterEntity toEntity(RuntimeCharacter runtime) {
        CharacterEntity entity = new CharacterEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        entity.setCurrentPlaceId(runtime.currentPlaceId());
        entity.setPreferences(JsonSerialization.toJson(runtime.preferences()));
        return entity;
    }

    public static RuntimeCharacter toRuntime(CharacterEntity entity) {
        return new RuntimeCharacter(
                entity.getId(),
                entity.getTitle(),
                JsonSerialization.toMap(entity.getSections()),
                entity.getCurrentPlaceId(),
                JsonSerialization.toMap(entity.getPreferences()));
    }
}
