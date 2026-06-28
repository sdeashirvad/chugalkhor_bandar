package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record PromptBudgetDto(
        List<SectionBudgetDto> sectionBudgets,
        int totalAvailableTokens,
        int reservedOutputTokens,
        int maxContextTokens) {}
