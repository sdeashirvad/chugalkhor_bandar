package com.chugalkhorbandar.application.behavior;

import com.chugalkhorbandar.application.session.SessionExpiredListener;
import org.springframework.stereotype.Component;

@Component
public class BehaviorEngineSessionCleanup implements SessionExpiredListener {

    private final InMemoryBehaviorProfileStore store;

    public BehaviorEngineSessionCleanup(InMemoryBehaviorProfileStore store) {
        this.store = store;
    }

    @Override
    public void onSessionExpired(String sessionId) {
        store.deleteBySessionId(sessionId);
    }
}
