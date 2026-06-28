package com.chugalkhorbandar.application.conversation.director;

import java.time.Instant;
import java.util.List;

public record ConversationExecutionResult(
        int deliveredCount,
        int cancelledCount,
        String interruptionReason,
        List<ConversationExecutionTimelineEntry> timeline,
        List<String> deliveredMessageIds,
        List<Integer> cancelledReplyIndexes,
        Instant startedAt,
        Instant completedAt,
        boolean interrupted,
        boolean completed) {

    public ConversationExecutionResult {
        timeline = List.copyOf(timeline == null ? List.of() : timeline);
        deliveredMessageIds = List.copyOf(deliveredMessageIds == null ? List.of() : deliveredMessageIds);
        cancelledReplyIndexes = List.copyOf(cancelledReplyIndexes == null ? List.of() : cancelledReplyIndexes);
        interruptionReason = interruptionReason == null ? "" : interruptionReason;
    }
}
