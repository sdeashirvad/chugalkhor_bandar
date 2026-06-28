package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record ContextPlanResponseDto(
        List<ContextSectionDto> sections,
        int totalEstimatedTokens,
        ContextPlanningTraceDto trace,
        List<KnowledgeFragmentTraceEntryDto> fragmentTrace) {}
