package com.chugalkhorbandar.application.conversation.director;

import com.chugalkhorbandar.application.conversation.ConversationWindowBuilder;
import com.chugalkhorbandar.application.context.ContextRequestFactory;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryProperties;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.session.SessionService;
import java.time.Instant;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ConversationDirectorService {

    private final SessionService sessionService;
    private final ContextRequestFactory contextRequestFactory;
    private final WorkingMemoryService workingMemoryService;
    private final WorkingMemoryProperties workingMemoryProperties;
    private final ConversationDirector director;
    private final InMemoryConversationPlanStore store;
    private final ConversationExecutionRegistry executionRegistry;

    public ConversationDirectorService(
            SessionService sessionService,
            @Lazy ContextRequestFactory contextRequestFactory,
            WorkingMemoryService workingMemoryService,
            WorkingMemoryProperties workingMemoryProperties,
            ConversationDirector director,
            InMemoryConversationPlanStore store,
            ConversationExecutionRegistry executionRegistry) {
        this.sessionService = sessionService;
        this.contextRequestFactory = contextRequestFactory;
        this.workingMemoryService = workingMemoryService;
        this.workingMemoryProperties = workingMemoryProperties;
        this.director = director;
        this.store = store;
        this.executionRegistry = executionRegistry;
    }

    public ConversationPlanSnapshot plan(String sessionId, String latestUserMessage) {
        sessionService.requireSession(sessionId);
        ConversationDirectorInput input = buildInput(sessionId, latestUserMessage);
        ConversationPlanSnapshot snapshot = director.plan(sessionId, input);
        store.save(snapshot);
        return snapshot;
    }

    public Optional<ConversationPlanSnapshot> getCurrentPlan(String sessionId) {
        sessionService.requireSession(sessionId);
        return store.findBySessionId(sessionId);
    }

    public void cancelPendingExecution(String sessionId, String reason) {
        executionRegistry.cancelPending(sessionId, reason);
    }

    public void markExecutionFinished(String sessionId, Instant planCreatedAt, ConversationExecutionResult result) {
        store.findBySessionId(sessionId).ifPresent(existing -> {
            if (!existing.plan().createdAt().equals(planCreatedAt)) {
                return;
            }
            ConversationPlan updatedPlan = existing.plan()
                    .withExecutionState(
                            result.interrupted(),
                            result.interrupted(),
                            result.startedAt(),
                            result.completedAt());
            store.save(existing.withExecutionResult(
                    updatedPlan,
                    result.deliveredCount(),
                    result.cancelledCount(),
                    result.interruptionReason(),
                    result.timeline(),
                    result.deliveredMessageIds(),
                    result.cancelledReplyIndexes(),
                    result.completed()));
        });
    }

    public void delete(String sessionId) {
        executionRegistry.cancelPending(sessionId, "Session closed");
        store.deleteBySessionId(sessionId);
    }

    public ConversationDirectorInput buildInput(String sessionId, String latestUserMessage) {
        var context = contextRequestFactory.create(sessionId, latestUserMessage);
        var workingMemory = workingMemoryService
                .find(sessionId)
                .map(snapshot -> snapshot.memory())
                .orElse(null);
        var window = ConversationWindowBuilder.build(
                context.currentConversation(), workingMemoryProperties.getConversationWindowMessages());
        return new ConversationDirectorInput(
                context.currentCharacter(), workingMemory, window, latestUserMessage);
    }
}
