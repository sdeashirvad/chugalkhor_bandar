package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.ConversationExecutionTimelineEntryDto;
import com.chugalkhorbandar.adapters.api.dto.ConversationPlanResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ConversationPlanningTraceEntryDto;
import com.chugalkhorbandar.application.conversation.director.ConversationExecutionTimelineEntry;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTraceEntry;

public final class ConversationDirectorDtoMapper {

    private ConversationDirectorDtoMapper() {}

    public static ConversationPlanResponseDto toDto(ConversationPlanSnapshot snapshot) {
        return new ConversationPlanResponseDto(
                snapshot.sessionId(),
                snapshot.plan().goal(),
                snapshot.plan().confidence(),
                snapshot.plan().continueConversation(),
                snapshot.plan().conversationEnergy(),
                snapshot.plan().conversationArc(),
                snapshot.plan().expectedMessageCount(),
                snapshot.plan().delays(),
                snapshot.plan().askFollowUpQuestion(),
                snapshot.plan().tellStory(),
                snapshot.plan().tellJoke(),
                snapshot.plan().tellMemory(),
                snapshot.plan().endConversation(),
                snapshot.plan().suggestedTone(),
                snapshot.plan().outcome(),
                snapshot.plan().createdAt(),
                snapshot.plan().isInterrupted(),
                snapshot.plan().isCancelled(),
                snapshot.plan().startedAt(),
                snapshot.plan().completedAt(),
                snapshot.executed(),
                snapshot.executedMessageCount(),
                snapshot.cancelledMessageCount(),
                snapshot.interruptionReason(),
                snapshot.timeline().stream().map(ConversationDirectorDtoMapper::toDto).toList(),
                snapshot.deliveredMessageIds(),
                snapshot.cancelledReplyIndexes(),
                snapshot.trace().entries().stream().map(ConversationDirectorDtoMapper::toDto).toList());
    }

    private static ConversationExecutionTimelineEntryDto toDto(ConversationExecutionTimelineEntry entry) {
        return new ConversationExecutionTimelineEntryDto(entry.replyIndex(), entry.event(), entry.at(), entry.delayMs());
    }

    private static ConversationPlanningTraceEntryDto toDto(ConversationPlanningTraceEntry entry) {
        return new ConversationPlanningTraceEntryDto(entry.rule(), entry.reason());
    }
}
