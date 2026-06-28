package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.StoryEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;

public final class StoryMapper {

    private StoryMapper() {}

    public static StoryEntity toEntity(RuntimeStory runtime) {
        StoryEntity entity = new StoryEntity();
        entity.setId(runtime.id());
        entity.setTitle(runtime.title());
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        entity.setLinkedStories(JsonSerialization.toJson(runtime.linkedStories()));
        return entity;
    }

    public static RuntimeStory toRuntime(StoryEntity entity) {
        return new RuntimeStory(
                entity.getId(),
                entity.getTitle(),
                JsonSerialization.toMap(entity.getSections()),
                JsonSerialization.toMap(entity.getLinkedStories()));
    }
}
