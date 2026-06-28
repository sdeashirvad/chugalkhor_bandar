package com.chugalkhorbandar.application.prompt.budget;

import java.util.List;

public record PromptBudget(
        List<SectionBudget> sectionBudgets,
        int totalAvailableTokens,
        int reservedOutputTokens,
        int maxContextTokens) {

    public PromptBudget {
        sectionBudgets = List.copyOf(sectionBudgets);
    }
}
