package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;

import com.chugalkhorbandar.domain.world.ports.TimelineRepository;
import com.chugalkhorbandar.domain.world.ports.query.TimelineQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTimelineEntry;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class InMemoryTimelineRepository implements TimelineRepository {

    private final InMemoryWorldStore store;

    public InMemoryTimelineRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void append(RuntimeTimelineEntry entry) {
        putUnique(store.timeline(), entry.id(), entry, "Timeline");
        store.timelineRecordedAt().put(entry.id(), store.nextTimelineTimestamp());
    }

    @Override
    public boolean exists(String entryId) {
        return store.timeline().containsKey(entryId);
    }

    @Override
    public Optional<RuntimeTimelineEntry> findById(String entryId) {
        return Optional.ofNullable(store.timeline().get(entryId));
    }

    @Override
    public List<RuntimeTimelineEntry> findAll(TimelineQuery query) {
        return store.timeline().values().stream()
                .filter(entry -> matchesChronology(entry, query.chronologyId()))
                .filter(entry -> matchesAfter(entry, query.after()))
                .filter(entry -> matchesBefore(entry, query.before()))
                .toList();
    }

    @Override
    public List<RuntimeTimelineEntry> findAfter(Instant point) {
        return store.timeline().values().stream()
                .filter(entry -> isRecordedAfter(entry, point))
                .toList();
    }

    @Override
    public List<RuntimeTimelineEntry> findBetween(Instant start, Instant end) {
        return store.timeline().values().stream()
                .filter(entry -> isRecordedBetween(entry, start, end))
                .toList();
    }

    @Override
    public Optional<RuntimeTimelineEntry> latest() {
        return store.timeline().values().stream()
                .max(Comparator.comparing(
                        entry -> recordedAt(entry.id()), Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(Optional::of)
                .orElse(Optional.empty());
    }

    private Instant recordedAt(String entryId) {
        return store.timelineRecordedAt().get(entryId);
    }

    private boolean matchesChronology(RuntimeTimelineEntry entry, String chronologyId) {
        return chronologyId == null || chronologyId.equals(entry.chronologyId());
    }

    private boolean matchesAfter(RuntimeTimelineEntry entry, Instant after) {
        if (after == null) {
            return true;
        }
        Instant recorded = recordedAt(entry.id());
        return recorded != null && recorded.isAfter(after);
    }

    private boolean matchesBefore(RuntimeTimelineEntry entry, Instant before) {
        if (before == null) {
            return true;
        }
        Instant recorded = recordedAt(entry.id());
        return recorded != null && recorded.isBefore(before);
    }

    private boolean isRecordedAfter(RuntimeTimelineEntry entry, Instant point) {
        Instant recorded = recordedAt(entry.id());
        return recorded != null && recorded.isAfter(point);
    }

    private boolean isRecordedBetween(RuntimeTimelineEntry entry, Instant start, Instant end) {
        Instant recorded = recordedAt(entry.id());
        if (recorded == null) {
            return false;
        }
        return !recorded.isBefore(start) && !recorded.isAfter(end);
    }
}
