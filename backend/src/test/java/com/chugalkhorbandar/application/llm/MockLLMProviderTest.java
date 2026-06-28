package com.chugalkhorbandar.application.llm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import com.chugalkhorbandar.config.LlmProperties;
import org.junit.jupiter.api.Test;

class MockLLMProviderTest {

    private final MockLLMProvider provider = new MockLLMProvider(new LlmProperties());

    @Test
    void returnsInspectionStyleReply() {
        ProviderRequest request = new ProviderRequest(
                List.of(
                        ProviderMessage.of(ProviderMessageRole.SYSTEM, "Personality\n\nCheerful.", "PERSONALITY"),
                        ProviderMessage.of(ProviderMessageRole.USER, "Where am I?", "USER_MESSAGE")),
                java.util.Map.of("sectionCount", "2"),
                0.7,
                1024,
                "mock-bandar");

        ProviderResponse response = provider.generateReply(request);

        assertThat(response.reply()).contains("[Mock Bandar]");
        assertThat(response.reply()).contains("- Personality");
        assertThat(response.reply()).contains("User asked:");
        assertThat(response.reply()).contains("\"Where am I?\"");
        assertThat(response.finishReason()).isEqualTo("stop");
        assertThat(response.tokenUsage().totalTokens()).isGreaterThan(0);
        assertThat(response.providerMetadata()).containsEntry("provider", "mock");
    }

    @Test
    void reportsHealthyProviderInfo() {
        LLMProviderInfo info = provider.providerInfo();

        assertThat(info.type()).isEqualTo(LLMProviderType.MOCK);
        assertThat(info.healthy()).isTrue();
        assertThat(provider.health()).isTrue();
    }

    @Test
    void exposesProviderCapabilities() {
        ProviderCapabilities capabilities = provider.capabilities();

        assertThat(capabilities.maxContextTokens()).isEqualTo(8192);
        assertThat(capabilities.reservedOutputTokens()).isEqualTo(1024);
        assertThat(capabilities.availablePromptTokens()).isEqualTo(7168);
        assertThat(capabilities.supportsSystemMessages()).isTrue();
        assertThat(capabilities.supportsMultiMessage()).isTrue();
    }
}
