package com.chugalkhorbandar.application.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.application.llm.groq.GroqProvider;
import com.chugalkhorbandar.config.LlmProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LLMProviderRegistryTest {

    @Test
    void returnsMockProviderWhenConfigured() {
        LlmProperties properties = new LlmProperties();
        properties.setProvider("mock");
        LLMProviderRegistry registry = registry(properties);

        assertThat(registry.configuredType()).isEqualTo(LLMProviderType.MOCK);
        assertThat(registry.activeProvider().providerType()).isEqualTo(LLMProviderType.MOCK);
        assertThat(registry.implementedProviders()).containsExactlyInAnyOrder(LLMProviderType.MOCK, LLMProviderType.GROQ);
        assertThat(registry.plannedProviders()).contains(LLMProviderType.GROQ, LLMProviderType.OPENAI);
    }

    @Test
    void returnsGroqProviderWhenConfigured() {
        LlmProperties properties = new LlmProperties();
        properties.setProvider("groq");
        LLMProviderRegistry registry = registry(properties);

        assertThat(registry.configuredType()).isEqualTo(LLMProviderType.GROQ);
        assertThat(registry.activeProvider().providerType()).isEqualTo(LLMProviderType.GROQ);
    }

    @Test
    void rejectsUnimplementedProvider() {
        LlmProperties properties = new LlmProperties();
        properties.setProvider("openai");
        LLMProviderRegistry registry = registry(properties);

        assertThat(registry.configuredType()).isEqualTo(LLMProviderType.OPENAI);
        assertThatThrownBy(registry::activeProvider)
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("OPENAI");
    }

    private static LLMProviderRegistry registry(LlmProperties properties) {
        GroqProvider groqProvider = Mockito.mock(GroqProvider.class);
        Mockito.when(groqProvider.providerType()).thenReturn(LLMProviderType.GROQ);
        return new LLMProviderRegistry(properties, new MockLLMProvider(properties), groqProvider);
    }
}
