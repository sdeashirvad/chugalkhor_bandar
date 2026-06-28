package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.TimelineEntryEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.TimelineEntryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.TimelineEntryMapper;
import com.chugalkhorbandar.domain.world.ports.TimelineRepository;
import com.chugalkhorbandar.domain.world.ports.query.TimelineQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTimelineEntry;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class PostgresTimelineRepository implements TimelineRepository {

    private final TimelineEntryJpaRepository jpa;

    public PostgresTimelineRepository(TimelineEntryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void append(RuntimeTimelineEntry entry) {
        ensureAbsent(jpa, entry.id(), "Timeline");
        TimelineEntryEntity entity = TimelineEntryMapper.toEntity(entry);
        entity.setRecordedAt(Instant.now());
        jpa.save(entity);
    }

    @Override
    public boolean exists(String entryId) {
        return jpa.existsById(entryId);
    }

    @Override
    public Optional<RuntimeTimelineEntry> findById(String entryId) {
        return jpa.findById(entryId).map(TimelineEntryMapper::toRuntime);
    }

    @Override
    public List<RuntimeTimelineEntry> findAll(TimelineQuery query) {
        return jpa.findAll().stream()
                .filter(entity -> matchesChronology(entity, query.chronologyId()))
                .filter(entity -> matchesAfter(entity, query.after()))
                .filter(entity -> matchesBefore(entity, query.before()))
                .map(TimelineEntryMapper::toRuntime)
                .toList();
    }

    @Override
    public List<RuntimeTimelineEntry> findAfter(Instant point) {
        return jpa.findAll().stream()
                .filter(entity -> isRecordedAfter(entity, point))
                .map(TimelineEntryMapper::toRuntime)
                .toList();
    }

    @Override
    public List<RuntimeTimelineEntry> findBetween(Instant start, Instant end) {
        return jpa.findAll().stream()
                .filter(entity -> isRecordedBetween(entity, start, end))
                .map(TimelineEntryMapper::toRuntime)
                .toList();
    }

    @Override
    public Optional<RuntimeTimelineEntry> latest() {
        return jpa.findAll().stream()
                .max(Comparator.comparing(
                        TimelineEntryEntity::getRecordedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(TimelineEntryMapper::toRuntime);
    }

    private static boolean matchesChronology(TimelineEntryEntity entity, String chronologyId) {
        return chronologyId == null || chronologyId.equals(entity.getChronologyId());
    }

    private static boolean matchesAfter(TimelineEntryEntity entity, Instant after) {
        if (after == null) {
            return true;
        }
        Instant recorded = entity.getRecordedAt();
        return recorded != null && recorded.isAfter(after);
    }

    private static boolean matchesBefore(TimelineEntryEntity entity, Instant before) {
        if (before == null) {
            return true;
        }
        Instant recorded = entity.getRecordedAt();
        return recorded != null && recorded.isBefore(before);
    }

    private static boolean isRecordedAfter(TimelineEntryEntity entity, Instant point) {
        Instant recorded = entity.getRecordedAt();
        return recorded != null && recorded.isAfter(point);
    }

    private static boolean isRecordedBetween(TimelineEntryEntity entity, Instant start, Instant end) {
        Instant recorded = entity.getRecordedAt();
        if (recorded == null) {
            return false;
        }
        return !recorded.isBefore(start) && !recorded.isAfter(end);
    }
}
