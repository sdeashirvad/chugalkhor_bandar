package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.GlossaryEntryEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeGlossaryEntry;

public final class GlossaryEntryMapper {

    private GlossaryEntryMapper() {}

    public static GlossaryEntryEntity toEntity(RuntimeGlossaryEntry runtime) {
        GlossaryEntryEntity entity = new GlossaryEntryEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimeGlossaryEntry toRuntime(GlossaryEntryEntity entity) {
        return new RuntimeGlossaryEntry(
                entity.getId(), entity.getTitle(), JsonSerialization.toMap(entity.getSections()));
    }
}
