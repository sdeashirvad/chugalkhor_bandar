package com.chugalkhorbandar.application.conversation.director;

import java.util.List;

public record ConversationPlanningTrace(List<ConversationPlanningTraceEntry> entries) {

    public ConversationPlanningTrace {
        entries = List.copyOf(entries == null ? List.of() : entries);
    }
}
