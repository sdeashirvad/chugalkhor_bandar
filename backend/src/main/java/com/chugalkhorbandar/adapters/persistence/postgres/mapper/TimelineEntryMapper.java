package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.JsonSerialization;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.TimelineEntryEntity;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTimelineEntry;

public final class TimelineEntryMapper {

    private TimelineEntryMapper() {}

    public static TimelineEntryEntity toEntity(RuntimeTimelineEntry runtime) {
        TimelineEntryEntity entity = new TimelineEntryEntity();
        entity.setId(runtime.id());
        entity.setChronologyId(runtime.chronologyId());
        entity.setTitle(runtime.title());
        entity.setTimelineEntries(JsonSerialization.toTimelineJson(runtime.timelineEntries()));
        entity.setSections(JsonSerialization.toJson(runtime.sections()));
        return entity;
    }

    public static RuntimeTimelineEntry toRuntime(TimelineEntryEntity entity) {
        return new RuntimeTimelineEntry(
                entity.getId(),
                entity.getChronologyId(),
                entity.getTitle(),
                JsonSerialization.toTimelineEntries(entity.getTimelineEntries()),
                JsonSerialization.toMap(entity.getSections()));
    }
}
