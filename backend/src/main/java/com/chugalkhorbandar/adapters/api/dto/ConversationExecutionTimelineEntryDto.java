package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;

public record ConversationExecutionTimelineEntryDto(
        int replyIndex, String event, Instant at, long delayMs) {}
