package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record WorldEventResponseDto(
        String id,
        String type,
        String title,
        String summary,
        List<String> participants,
        String visibility,
        Instant createdAt,
        LocalDate effectiveDate,
        Map<String, String> metadata,
        String status,
        String origin) {}
