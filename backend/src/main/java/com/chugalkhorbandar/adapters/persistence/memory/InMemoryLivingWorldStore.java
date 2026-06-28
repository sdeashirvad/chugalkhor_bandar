package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.world.living.WorldEvent;
import com.chugalkhorbandar.application.world.living.WorldEventType;
import com.chugalkhorbandar.application.world.living.WorldTickHistory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryLivingWorldStore {

    private final ConcurrentHashMap<String, WorldEvent> eventsById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, WorldTickHistory> tickHistoryByRunId = new ConcurrentHashMap<>();

    public WorldEvent saveEvent(WorldEvent event) {
        eventsById.put(event.id(), event);
        return event;
    }

    public Optional<WorldEvent> findEventById(String id) {
        return Optional.ofNullable(eventsById.get(id));
    }

    public List<WorldEvent> findAllEvents() {
        return eventsById.values().stream()
                .sorted(Comparator.comparing(WorldEvent::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<WorldEvent> findEventsByType(WorldEventType type) {
        return eventsById.values().stream()
                .filter(event -> event.type() == type)
                .sorted(Comparator.comparing(WorldEvent::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Set<String> findAllEventIds() {
        return new HashSet<>(eventsById.keySet());
    }

    public WorldTickHistory saveTickHistory(WorldTickHistory history) {
        tickHistoryByRunId.put(history.runId(), history);
        return history;
    }

    public Optional<WorldTickHistory> findLatestTickHistory() {
        return tickHistoryByRunId.values().stream()
                .max(Comparator.comparing(WorldTickHistory::startedAt));
    }

    public Optional<WorldTickHistory> findLatestTickHistoryByMode(
            com.chugalkhorbandar.application.world.living.WorldClockMode mode) {
        return tickHistoryByRunId.values().stream()
                .filter(history -> history.mode() == mode)
                .max(Comparator.comparing(WorldTickHistory::startedAt));
    }

    public List<WorldTickHistory> findAllTickHistory() {
        return tickHistoryByRunId.values().stream()
                .sorted(Comparator.comparing(WorldTickHistory::startedAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
