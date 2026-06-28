package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;

public record PromptBudgetResponseDto(
        ContextProfileDto profile,
        String selectionReason,
        List<BudgetedPromptSectionDto> sections,
        List<DroppedSectionDto> droppedSections,
        PromptBudgetDto budget,
        int totalPromptTokens,
        int remainingBudget,
        ProviderCapabilitiesDto providerCapabilities) {}
