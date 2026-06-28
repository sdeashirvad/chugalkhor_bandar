package com.chugalkhorbandar.application.memory.working;

import com.chugalkhorbandar.application.session.SessionExpiredListener;
import com.chugalkhorbandar.domain.memory.ports.WorkingMemoryRepository;
import org.springframework.stereotype.Component;

@Component
public class WorkingMemorySessionCleanup implements SessionExpiredListener {

    private final WorkingMemoryRepository repository;

    public WorkingMemorySessionCleanup(WorkingMemoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onSessionExpired(String sessionId) {
        repository.deleteBySessionId(sessionId);
    }
}
