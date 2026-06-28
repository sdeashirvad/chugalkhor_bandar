package com.chugalkhorbandar.application.prompt.budget;

import com.chugalkhorbandar.application.prompt.PromptSectionType;

public record SectionBudget(
        PromptSectionType sectionType,
        int maxTokens,
        int minimumTokens,
        int priority,
        boolean required) {}
