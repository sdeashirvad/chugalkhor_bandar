package com.chugalkhorbandar.application.conversation.director;

import java.time.Instant;
import java.util.List;

public record ConversationPlan(
        ConversationGoal goal,
        double confidence,
        boolean continueConversation,
        ConversationEnergy conversationEnergy,
        ConversationArc conversationArc,
        int expectedMessageCount,
        List<Long> delays,
        boolean askFollowUpQuestion,
        boolean tellStory,
        boolean tellJoke,
        boolean tellMemory,
        boolean endConversation,
        String suggestedTone,
        ConversationOutcome outcome,
        Instant createdAt,
        boolean isInterrupted,
        boolean isCancelled,
        Instant startedAt,
        Instant completedAt) {

    public ConversationPlan {
        delays = List.copyOf(delays == null ? List.of() : delays);
        expectedMessageCount = Math.min(3, Math.max(1, expectedMessageCount));
        suggestedTone = suggestedTone == null ? "" : suggestedTone;
    }

    public ConversationPlan withExecutionState(
            boolean interrupted, boolean cancelled, Instant started, Instant completed) {
        return new ConversationPlan(
                goal,
                confidence,
                continueConversation,
                conversationEnergy,
                conversationArc,
                expectedMessageCount,
                delays,
                askFollowUpQuestion,
                tellStory,
                tellJoke,
                tellMemory,
                endConversation,
                suggestedTone,
                outcome,
                createdAt,
                interrupted,
                cancelled,
                started,
                completed);
    }
}
