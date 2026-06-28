package com.chugalkhorbandar.application.prompt.budget;

import com.chugalkhorbandar.application.llm.LLMProviderRegistry;
import com.chugalkhorbandar.application.llm.ProviderCapabilities;
import com.chugalkhorbandar.application.prompt.profile.PromptProfileResult;
import com.chugalkhorbandar.application.prompt.profile.PromptProfileService;
import org.springframework.stereotype.Service;

@Service
public class PromptBudgetService {

    private final PromptProfileService promptProfileService;
    private final BudgetAllocator budgetAllocator;
    private final LLMProviderRegistry providerRegistry;

    public PromptBudgetService(
            PromptProfileService promptProfileService,
            BudgetAllocator budgetAllocator,
            LLMProviderRegistry providerRegistry) {
        this.promptProfileService = promptProfileService;
        this.budgetAllocator = budgetAllocator;
        this.providerRegistry = providerRegistry;
    }

    public PromptBudgetResult allocate(String sessionId, String latestMessage) {
        PromptProfileResult profileResult = promptProfileService.selectProfile(sessionId, latestMessage);
        ProviderCapabilities capabilities = providerRegistry.activeProvider().capabilities();
        BudgetedPrompt budgetedPrompt = budgetAllocator.allocate(
                profileResult.pipeline().composedPrompt(),
                profileResult.selection().profile(),
                capabilities);
        return new PromptBudgetResult(profileResult.selection(), budgetedPrompt, capabilities);
    }
}
