package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;

public record DeliveryHistoryResponseDto(
        String id,
        String reportId,
        String recipient,
        String status,
        String provider,
        String providerMessageId,
        int attempt,
        long latencyMs,
        String error,
        Instant createdAt) {}
