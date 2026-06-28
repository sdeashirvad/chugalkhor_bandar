package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.ports.query.TimelineQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTimelineEntry;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TimelineRepository {

    void append(RuntimeTimelineEntry entry);

    boolean exists(String entryId);

    Optional<RuntimeTimelineEntry> findById(String entryId);

    List<RuntimeTimelineEntry> findAll(TimelineQuery query);

    List<RuntimeTimelineEntry> findAfter(Instant point);

    List<RuntimeTimelineEntry> findBetween(Instant start, Instant end);

    Optional<RuntimeTimelineEntry> latest();
}
