package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record PromptSectionDto(
        String sectionType,
        String title,
        int priority,
        boolean required,
        int estimatedTokens,
        String content,
        String fragmentId,
        String fragmentType) {}
