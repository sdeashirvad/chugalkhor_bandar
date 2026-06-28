package com.chugalkhorbandar.application.conversation.director;

import com.chugalkhorbandar.application.behavior.BehaviorContext;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.application.llm.LLMService;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import com.chugalkhorbandar.domain.conversation.ports.ConversationMessageRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ConversationPlanExecutor {

    private final LLMService llmService;
    private final ConversationPlanContext planContext;
    private final ConversationDirectorService directorService;
    private final ConversationMessageRepository messages;
    private final ConversationExecutionRegistry executionRegistry;
    private final BehaviorEngineService behaviorEngineService;
    private final BehaviorContext behaviorContext;

    public ConversationPlanExecutor(
            LLMService llmService,
            ConversationPlanContext planContext,
            ConversationDirectorService directorService,
            ConversationMessageRepository messages,
            ConversationExecutionRegistry executionRegistry,
            BehaviorEngineService behaviorEngineService,
            BehaviorContext behaviorContext) {
        this.llmService = llmService;
        this.planContext = planContext;
        this.directorService = directorService;
        this.messages = messages;
        this.executionRegistry = executionRegistry;
        this.behaviorEngineService = behaviorEngineService;
        this.behaviorContext = behaviorContext;
    }

    public List<ConversationMessage> execute(
            ConversationPlanSnapshot snapshot, String sessionId, String userMessage, Conversation conversation) {
        ConversationPlan plan = snapshot.plan();
        int messageCount = plan.expectedMessageCount();
        ConversationExecutionHandle handle = executionRegistry.begin(sessionId);
        Instant startedAt = Instant.now();

        List<ConversationMessage> bandarMessages = new ArrayList<>();
        List<ConversationExecutionTimelineEntry> timeline = new ArrayList<>();
        List<String> deliveredMessageIds = new ArrayList<>();
        List<Integer> cancelledReplyIndexes = new ArrayList<>();
        timeline.add(new ConversationExecutionTimelineEntry(0, "EXECUTION_STARTED", startedAt, 0));

        behaviorEngineService.getCurrentProfile(sessionId).ifPresent(behaviorSnapshot -> behaviorContext.activate(behaviorSnapshot.profile()));

        try {
            for (int replyIndex = 0; replyIndex < messageCount; replyIndex++) {
                if (handle.isCancelled()) {
                    addCancelledIndexes(cancelledReplyIndexes, replyIndex, messageCount);
                    break;
                }
                if (replyIndex > 0) {
                    long delayMs = plan.delays().get(replyIndex - 1);
                    timeline.add(new ConversationExecutionTimelineEntry(
                            replyIndex, "DELAY_SCHEDULED", Instant.now(), delayMs));
                    if (!interruptibleSleep(delayMs, handle)) {
                        addCancelledIndexes(cancelledReplyIndexes, replyIndex, messageCount);
                        break;
                    }
                }
                if (handle.isCancelled()) {
                    addCancelledIndexes(cancelledReplyIndexes, replyIndex, messageCount);
                    break;
                }

                planContext.activate(plan, replyIndex);
                timeline.add(new ConversationExecutionTimelineEntry(
                        replyIndex, "LLM_GENERATE", Instant.now(), 0));
                String reply = llmService.generate(sessionId, userMessage).providerResponse().reply();
                ConversationMessage bandarMessage = new ConversationMessage(
                        UUID.randomUUID().toString(),
                        conversation.conversationId(),
                        Sender.BANDAR,
                        Instant.now(),
                        reply,
                        Visibility.PUBLIC,
                        Map.of("directorReplyIndex", String.valueOf(replyIndex)));
                ConversationMessage persisted = messages.append(bandarMessage);
                bandarMessages.add(persisted);
                deliveredMessageIds.add(persisted.messageId());
                timeline.add(new ConversationExecutionTimelineEntry(
                        replyIndex, "MESSAGE_DELIVERED", Instant.now(), 0));
            }
        } finally {
            planContext.clear();
            behaviorContext.clear();
            Instant completedAt = Instant.now();
            boolean interrupted = handle.isCancelled() || !cancelledReplyIndexes.isEmpty();
            boolean completed = !interrupted && bandarMessages.size() == messageCount;
            directorService.markExecutionFinished(
                    sessionId,
                    plan.createdAt(),
                    new ConversationExecutionResult(
                            bandarMessages.size(),
                            cancelledReplyIndexes.size(),
                            handle.interruptionReason(),
                            timeline,
                            deliveredMessageIds,
                            cancelledReplyIndexes,
                            startedAt,
                            completedAt,
                            interrupted,
                            completed));
            executionRegistry.complete(sessionId, handle);
        }
        return bandarMessages;
    }

    private static void addCancelledIndexes(List<Integer> cancelledReplyIndexes, int fromIndex, int messageCount) {
        for (int index = fromIndex; index < messageCount; index++) {
            cancelledReplyIndexes.add(index);
        }
    }

    private static boolean interruptibleSleep(long delayMs, ConversationExecutionHandle handle) {
        if (delayMs <= 0) {
            return !handle.isCancelled();
        }
        long remaining = delayMs;
        while (remaining > 0) {
            if (handle.isCancelled()) {
                return false;
            }
            long chunk = Math.min(remaining, 50L);
            try {
                Thread.sleep(chunk);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return false;
            }
            remaining -= chunk;
        }
        return !handle.isCancelled();
    }
}
