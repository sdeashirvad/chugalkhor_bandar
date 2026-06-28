package com.chugalkhorbandar.application.prompt.budget;

import com.chugalkhorbandar.application.prompt.profile.ContextProfileType;
import java.util.List;

public record BudgetedPrompt(
        ContextProfileType profileType,
        List<BudgetedPromptSection> sections,
        List<DroppedSection> droppedSections,
        PromptBudget budget,
        int totalPromptTokens,
        int remainingBudget) {

    public BudgetedPrompt {
        sections = List.copyOf(sections);
        droppedSections = List.copyOf(droppedSections);
    }
}
