package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record ResolvedContextResponseDto(
        List<ResolvedContextSectionDto> sections,
        List<KnowledgeFragmentDto> fragments,
        int totalEstimatedTokens) {}
