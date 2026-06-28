package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;

public record ConversationResponseDto(
        String conversationId,
        String sessionId,
        ConversationCharacterDto currentCharacter,
        Instant startedAt,
        Instant lastActivity,
        String status) {}
