package com.chugalkhorbandar.application.conversation.director;

import java.time.Instant;

public record ConversationExecutionTimelineEntry(
        int replyIndex,
        String event,
        Instant at,
        long delayMs) {}
