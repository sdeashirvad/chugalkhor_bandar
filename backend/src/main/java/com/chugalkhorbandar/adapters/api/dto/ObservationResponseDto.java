package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.cognition.ObservationType;
import java.time.Instant;
import java.util.Map;

public record ObservationResponseDto(
        String id,
        ObservationType type,
        double confidence,
        String summary,
        String evidence,
        Map<String, String> metadata,
        Instant createdAt) {}
