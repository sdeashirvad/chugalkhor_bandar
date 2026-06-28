package com.chugalkhorbandar.application.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.application.llm.groq.GroqProvider;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptComposer;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.application.prompt.budget.BudgetedPrompt;
import com.chugalkhorbandar.application.prompt.budget.BudgetedPromptSection;
import com.chugalkhorbandar.application.prompt.budget.PromptBudget;
import com.chugalkhorbandar.application.prompt.budget.PromptBudgetResult;
import com.chugalkhorbandar.application.prompt.budget.PromptBudgetService;
import com.chugalkhorbandar.application.prompt.budget.SectionBudget;
import com.chugalkhorbandar.application.prompt.profile.ContextProfile;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileCatalog;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileType;
import com.chugalkhorbandar.application.prompt.profile.ProfileSelection;
import com.chugalkhorbandar.config.LlmProperties;
import com.chugalkhorbandar.config.PromptProfileProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LLMServiceTest {

    @Mock
    private PromptBudgetService promptBudgetService;

    private LLMService llmService;

    @BeforeEach
    void setUp() {
        LlmProperties llmProperties = new LlmProperties();
        MockLLMProvider mockProvider = new MockLLMProvider(llmProperties);
        LLMProviderRegistry registry =
                new LLMProviderRegistry(llmProperties, mockProvider, org.mockito.Mockito.mock(GroqProvider.class));
        PromptToProviderAdapter adapter = new PromptToProviderAdapter(llmProperties);
        llmService = new LLMService(promptBudgetService, adapter, registry);
    }

    @Test
    void runsBudgetAdaptAndGeneratePipeline() {
        ComposedPrompt composedPrompt = new ComposedPrompt(List.of(
                PromptSection.of(PromptSectionType.PERSONALITY, "Personality", true, "Cheerful."),
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?"),
                PromptSection.of(
                        PromptSectionType.INSTRUCTIONS,
                        "Instructions",
                        true,
                        PromptComposer.DEFAULT_INSTRUCTION)));
        ContextProfile profile =
                new ContextProfileCatalog(new PromptProfileProperties()).profile(ContextProfileType.LOCATION_QUERY);
        BudgetedPrompt budgetedPrompt = new BudgetedPrompt(
                ContextProfileType.LOCATION_QUERY,
                composedPrompt.sections().stream()
                        .map(section -> new BudgetedPromptSection(
                                section,
                                new SectionBudget(section.sectionType(), 128, 16, section.priority(), section.required()),
                                false,
                                section.estimatedTokens()))
                        .toList(),
                List.of(),
                new PromptBudget(List.of(), 8192, 1024, 8192),
                composedPrompt.totalEstimatedTokens(),
                7000);
        when(promptBudgetService.allocate(eq("session-1"), eq("Where am I?")))
                .thenReturn(new PromptBudgetResult(
                        new ProfileSelection(profile, "test"), budgetedPrompt, mockProviderCapabilities()));

        LLMGenerateResult result = llmService.generate("session-1", "Where am I?");

        assertThat(result.providerInfo().type()).isEqualTo(LLMProviderType.MOCK);
        assertThat(result.providerRequest().messages()).isNotEmpty();
        assertThat(result.providerResponse().reply()).contains("[Mock Bandar]");
    }

    private static ProviderCapabilities mockProviderCapabilities() {
        return new ProviderCapabilities(8192, 1024, true, true);
    }
}
