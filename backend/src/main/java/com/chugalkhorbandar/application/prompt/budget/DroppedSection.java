package com.chugalkhorbandar.application.prompt.budget;

import com.chugalkhorbandar.application.prompt.PromptSectionType;

public record DroppedSection(
        PromptSectionType sectionType,
        String title,
        int estimatedTokens,
        String reason) {}
