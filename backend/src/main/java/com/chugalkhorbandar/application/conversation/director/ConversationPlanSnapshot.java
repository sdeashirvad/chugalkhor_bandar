package com.chugalkhorbandar.application.conversation.director;

import java.util.List;

public record ConversationPlanSnapshot(
        String sessionId,
        ConversationPlan plan,
        ConversationPlanningTrace trace,
        boolean executed,
        int executedMessageCount,
        int cancelledMessageCount,
        String interruptionReason,
        List<ConversationExecutionTimelineEntry> timeline,
        List<String> deliveredMessageIds,
        List<Integer> cancelledReplyIndexes) {

    public ConversationPlanSnapshot {
        timeline = List.copyOf(timeline == null ? List.of() : timeline);
        deliveredMessageIds = List.copyOf(deliveredMessageIds == null ? List.of() : deliveredMessageIds);
        cancelledReplyIndexes = List.copyOf(cancelledReplyIndexes == null ? List.of() : cancelledReplyIndexes);
        interruptionReason = interruptionReason == null ? "" : interruptionReason;
    }

    public static ConversationPlanSnapshot planned(
            String sessionId, ConversationPlan plan, ConversationPlanningTrace trace) {
        return new ConversationPlanSnapshot(
                sessionId, plan, trace, false, 0, 0, "", List.of(), List.of(), List.of());
    }

    public ConversationPlanSnapshot withExecutionResult(
            ConversationPlan updatedPlan,
            int deliveredCount,
            int cancelledCount,
            String reason,
            List<ConversationExecutionTimelineEntry> executionTimeline,
            List<String> messageIds,
            List<Integer> cancelledIndexes,
            boolean completed) {
        return new ConversationPlanSnapshot(
                sessionId,
                updatedPlan,
                trace,
                completed,
                deliveredCount,
                cancelledCount,
                reason == null ? "" : reason,
                executionTimeline,
                messageIds,
                cancelledIndexes);
    }
}
