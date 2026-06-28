package com.chugalkhorbandar.application.memory.working;

import com.chugalkhorbandar.application.context.ContextRequestFactory;
import com.chugalkhorbandar.application.session.SessionExpiredListener;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.domain.memory.ports.WorkingMemoryRepository;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class WorkingMemoryService {

    private final SessionService sessionService;
    private final ContextRequestFactory contextRequestFactory;
    private final WorkingMemoryBuilder builder;
    private final WorkingMemoryRepository repository;

    public WorkingMemoryService(
            SessionService sessionService,
            @Lazy ContextRequestFactory contextRequestFactory,
            WorkingMemoryBuilder builder,
            WorkingMemoryRepository repository) {
        this.sessionService = sessionService;
        this.contextRequestFactory = contextRequestFactory;
        this.builder = builder;
        this.repository = repository;
    }

    public Optional<WorkingMemorySnapshot> find(String sessionId) {
        return repository.findBySessionId(sessionId);
    }

    public WorkingMemorySnapshot getOrBuild(String sessionId) {
        sessionService.requireSession(sessionId);
        return repository.findBySessionId(sessionId).orElseGet(() -> rebuild(sessionId));
    }

    public WorkingMemorySnapshot rebuildFromContext(com.chugalkhorbandar.application.context.ContextPlannerRequest context) {
        long previousVersion = repository.findBySessionId(context.session().sessionId())
                .map(snapshot -> snapshot.memory().version())
                .orElse(0L);
        WorkingMemorySnapshot snapshot = builder.build(
                context.currentCharacter(),
                context.session(),
                context.currentConversation(),
                context.runtimeWorld(),
                previousVersion);
        return repository.save(snapshot);
    }

    public WorkingMemorySnapshot rebuild(String sessionId) {
        var context = contextRequestFactory.create(sessionId, "");
        long previousVersion = repository.findBySessionId(sessionId)
                .map(snapshot -> snapshot.memory().version())
                .orElse(0L);
        WorkingMemorySnapshot snapshot = builder.build(
                context.currentCharacter(),
                context.session(),
                context.currentConversation(),
                context.runtimeWorld(),
                previousVersion);
        return repository.save(snapshot);
    }

    public void delete(String sessionId) {
        repository.deleteBySessionId(sessionId);
    }
}
