package com.chugalkhorbandar.application.llm;

import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.budget.BudgetedPrompt;
import com.chugalkhorbandar.application.prompt.budget.BudgetedPromptSection;
import com.chugalkhorbandar.application.prompt.budget.PromptBudgetResult;
import com.chugalkhorbandar.application.prompt.budget.PromptBudgetService;
import org.springframework.stereotype.Service;

@Service
public class LLMService {

    private final PromptBudgetService promptBudgetService;
    private final PromptToProviderAdapter promptToProviderAdapter;
    private final LLMProviderRegistry providerRegistry;

    public LLMService(
            PromptBudgetService promptBudgetService,
            PromptToProviderAdapter promptToProviderAdapter,
            LLMProviderRegistry providerRegistry) {
        this.promptBudgetService = promptBudgetService;
        this.promptToProviderAdapter = promptToProviderAdapter;
        this.providerRegistry = providerRegistry;
    }

    public LLMGenerateResult generate(String sessionId, String latestMessage) {
        PromptBudgetResult budgetResult = promptBudgetService.allocate(sessionId, latestMessage);
        ComposedPrompt composedPrompt = toComposedPrompt(budgetResult.budgetedPrompt());
        ProviderRequest providerRequest = promptToProviderAdapter.adapt(composedPrompt);
        LLMProvider provider = providerRegistry.activeProvider();
        ProviderResponse providerResponse = provider.generateReply(providerRequest);
        return new LLMGenerateResult(provider.providerInfo(), providerRequest, providerResponse);
    }

    private static ComposedPrompt toComposedPrompt(BudgetedPrompt budgetedPrompt) {
        return new ComposedPrompt(
                budgetedPrompt.sections().stream().map(BudgetedPromptSection::section).toList());
    }
}
