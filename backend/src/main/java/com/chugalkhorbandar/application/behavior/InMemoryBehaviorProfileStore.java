package com.chugalkhorbandar.application.behavior;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemoryBehaviorProfileStore {

    private final ConcurrentHashMap<String, BehaviorProfileSnapshot> snapshotsBySession = new ConcurrentHashMap<>();

    public Optional<BehaviorProfileSnapshot> findBySessionId(String sessionId) {
        return Optional.ofNullable(snapshotsBySession.get(sessionId));
    }

    public BehaviorProfileSnapshot save(BehaviorProfileSnapshot snapshot) {
        snapshotsBySession.put(snapshot.sessionId(), snapshot);
        return snapshot;
    }

    public void deleteBySessionId(String sessionId) {
        snapshotsBySession.remove(sessionId);
    }
}
