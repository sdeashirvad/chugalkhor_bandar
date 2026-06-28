package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record PromptInspectionDto(
        List<PromptInspectionEntryDto> sections,
        int totalEstimatedTokens,
        int requiredSectionCount,
        int optionalSectionCount) {}
