package com.chugalkhorbandar.application.prompt.budget;

import com.chugalkhorbandar.application.llm.ProviderCapabilities;
import com.chugalkhorbandar.application.prompt.profile.ProfileSelection;

public record PromptBudgetResult(
        ProfileSelection profileSelection, BudgetedPrompt budgetedPrompt, ProviderCapabilities capabilities) {}
