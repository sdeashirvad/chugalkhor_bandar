package com.chugalkhorbandar.adapters.api.dto;

public record BudgetedPromptSectionDto(
        PromptSectionDto section,
        SectionBudgetDto budget,
        boolean truncated,
        int allocatedTokens) {}
