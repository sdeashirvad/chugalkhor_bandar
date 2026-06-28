package com.chugalkhorbandar.adapters.api.dto;

import com.chugalkhorbandar.application.cognition.RecommendationAction;
import java.util.Map;

public record RecommendationResponseDto(
        String id,
        RecommendationAction action,
        double confidence,
        String reason,
        String target,
        Map<String, String> metadata) {}
