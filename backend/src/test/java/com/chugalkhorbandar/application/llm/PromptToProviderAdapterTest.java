package com.chugalkhorbandar.application.llm;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptComposer;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PromptToProviderAdapterTest {

    private PromptToProviderAdapter adapter;

    @BeforeEach
    void setUp() {
        LlmProperties properties = new LlmProperties();
        properties.setModel("mock-bandar");
        properties.setTemperature(0.5);
        properties.setMaxOutputTokens(512);
        adapter = new PromptToProviderAdapter(properties);
    }

    @Test
    void mapsSectionsToRoleSpecificMessages() {
        ComposedPrompt composed = new ComposedPrompt(List.of(
                PromptSection.of(PromptSectionType.PERSONALITY, "Personality", true, "Cheerful bandar."),
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?"),
                PromptSection.of(
                        PromptSectionType.INSTRUCTIONS,
                        "Instructions",
                        true,
                        PromptComposer.DEFAULT_INSTRUCTION)));

        ProviderRequest request = adapter.adapt(composed);

        assertThat(request.model()).isEqualTo("mock-bandar");
        assertThat(request.temperature()).isEqualTo(0.5);
        assertThat(request.maxOutputTokens()).isEqualTo(512);
        assertThat(request.messages()).hasSize(3);
        assertThat(request.messages().get(0).role()).isEqualTo(ProviderMessageRole.SYSTEM);
        assertThat(request.messages().get(0).content()).contains("Your Character");
        assertThat(request.messages().get(1).role()).isEqualTo(ProviderMessageRole.USER);
        assertThat(request.messages().get(1).content()).isEqualTo("Where am I?");
        assertThat(request.messages().get(2).role()).isEqualTo(ProviderMessageRole.SYSTEM);
        assertThat(request.messages().get(2).sectionType()).isEqualTo("INSTRUCTIONS");
    }

    @Test
    void parsesConversationHistoryIntoTurnMessages() {
        ComposedPrompt composed = new ComposedPrompt(List.of(
                PromptSection.of(
                        PromptSectionType.CURRENT_CONVERSATION,
                        "Current Conversation",
                        true,
                        "USER: Hello\nBANDAR: Welcome."),
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?")));

        ProviderRequest request = adapter.adapt(composed);

        assertThat(request.messages()).extracting(ProviderMessage::role)
                .containsExactly(ProviderMessageRole.USER, ProviderMessageRole.ASSISTANT, ProviderMessageRole.USER);
        assertThat(request.messages().get(0).content()).isEqualTo("Hello");
        assertThat(request.messages().get(1).content()).isEqualTo("Welcome.");
    }
}
