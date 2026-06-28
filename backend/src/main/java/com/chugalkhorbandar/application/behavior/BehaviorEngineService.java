package com.chugalkhorbandar.application.behavior;

import com.chugalkhorbandar.application.conversation.ConversationWindowBuilder;
import com.chugalkhorbandar.application.context.ContextRequestFactory;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryProperties;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.session.SessionService;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class BehaviorEngineService {

    private final SessionService sessionService;
    private final ContextRequestFactory contextRequestFactory;
    private final WorkingMemoryService workingMemoryService;
    private final WorkingMemoryProperties workingMemoryProperties;
    private final BehaviorEngine behaviorEngine;
    private final InMemoryBehaviorProfileStore store;

    public BehaviorEngineService(
            SessionService sessionService,
            @Lazy ContextRequestFactory contextRequestFactory,
            WorkingMemoryService workingMemoryService,
            WorkingMemoryProperties workingMemoryProperties,
            BehaviorEngine behaviorEngine,
            InMemoryBehaviorProfileStore store) {
        this.sessionService = sessionService;
        this.contextRequestFactory = contextRequestFactory;
        this.workingMemoryService = workingMemoryService;
        this.workingMemoryProperties = workingMemoryProperties;
        this.behaviorEngine = behaviorEngine;
        this.store = store;
    }

    public BehaviorProfileSnapshot select(String sessionId, String latestUserMessage, ConversationPlan conversationPlan) {
        sessionService.requireSession(sessionId);
        BehaviorEngineInput input = buildInput(sessionId, latestUserMessage, conversationPlan);
        BehaviorProfileSnapshot snapshot = behaviorEngine.select(sessionId, input);
        store.save(snapshot);
        return snapshot;
    }

    public Optional<BehaviorProfileSnapshot> getCurrentProfile(String sessionId) {
        sessionService.requireSession(sessionId);
        return store.findBySessionId(sessionId);
    }

    public void delete(String sessionId) {
        store.deleteBySessionId(sessionId);
    }

    private BehaviorEngineInput buildInput(String sessionId, String latestUserMessage, ConversationPlan conversationPlan) {
        var context = contextRequestFactory.create(sessionId, latestUserMessage);
        var workingMemory = workingMemoryService
                .find(sessionId)
                .map(snapshot -> snapshot.memory())
                .orElse(null);
        var window = ConversationWindowBuilder.build(
                context.currentConversation(), workingMemoryProperties.getConversationWindowMessages());
        return new BehaviorEngineInput(
                context.currentCharacter(),
                workingMemory,
                conversationPlan,
                window,
                context.runtimeWorld(),
                latestUserMessage);
    }
}
