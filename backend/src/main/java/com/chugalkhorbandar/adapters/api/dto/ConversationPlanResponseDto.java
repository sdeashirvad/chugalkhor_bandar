package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import java.time.Instant;
import java.util.List;

public record ConversationPlanResponseDto(
        String sessionId,
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
        Instant completedAt,
        boolean executed,
        int executedMessageCount,
        int cancelledMessageCount,
        String interruptionReason,
        List<ConversationExecutionTimelineEntryDto> timeline,
        List<String> deliveredMessageIds,
        List<Integer> cancelledReplyIndexes,
        List<ConversationPlanningTraceEntryDto> trace) {}
