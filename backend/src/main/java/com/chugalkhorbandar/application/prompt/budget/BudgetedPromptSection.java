package com.chugalkhorbandar.application.prompt.budget;

import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileType;

public record BudgetedPromptSection(
        PromptSection section, SectionBudget budget, boolean truncated, int allocatedTokens) {}
