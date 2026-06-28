package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;

public record SessionResponseDto(
        String sessionId,
        CurrentCharacterDto currentCharacter,
        Instant startedAt,
        Instant lastActivity,
        String status) {}
