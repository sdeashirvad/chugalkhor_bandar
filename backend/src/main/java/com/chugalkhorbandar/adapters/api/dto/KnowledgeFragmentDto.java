package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Set;

public record KnowledgeFragmentDto(
        String fragmentId,
        String fragmentType,
        String title,
        String content,
        String sourceDocument,
        String sourceSection,
        int estimatedTokens,
        Set<String> tags,
        double confidence,
        String selectionReason) {}
