package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record ComposedPromptResponseDto(
        List<PromptSectionDto> sections,
        int totalEstimatedTokens,
        int requiredSectionCount,
        int optionalSectionCount,
        PromptInspectionDto inspection,
        List<LlmPromptMessageDto> llmMessages) {}
