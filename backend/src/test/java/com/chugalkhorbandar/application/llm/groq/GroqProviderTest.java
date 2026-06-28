package com.chugalkhorbandar.application.llm.groq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.application.llm.ProviderException;
import com.chugalkhorbandar.application.llm.ProviderMessage;
import com.chugalkhorbandar.application.llm.ProviderMessageRole;
import com.chugalkhorbandar.application.llm.ProviderRequest;
import com.chugalkhorbandar.application.llm.ProviderResponse;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroqProviderTest {

    @Mock
    private GroqHttpClient httpClient;

    private GroqProvider provider;
    private GroqKeyPool keyPool;

    @BeforeEach
    void setUp() {
        LlmProperties llmProperties = new LlmProperties();
        llmProperties.setModel("llama-test");
        keyPool = new GroqKeyPool(List.of("key-one", "key-two"));
        provider = new GroqProvider(keyPool, httpClient, llmProperties);
    }

    @Test
    void mapsSuccessfulResponseWithKeyIndex() throws Exception {
        when(httpClient.chatCompletion(eq("key-one"), any()))
                .thenReturn(new GroqHttpClient.GroqChatCompletionResult("Bandar speaks.", "stop", 10, 4, 14));

        ProviderResponse response = provider.generateReply(sampleRequest());

        assertThat(response.reply()).isEqualTo("Bandar speaks.");
        assertThat(response.providerMetadata()).containsEntry("provider", "groq");
        assertThat(response.providerMetadata()).containsEntry("keyIndex", "1");
        assertThat(response.providerMetadata()).containsEntry("retryCount", "0");
        assertThat(response.tokenUsage().promptTokens()).isEqualTo(10);
    }

    @Test
    void retriesOnceWithAlternateKeyOnRateLimit() throws Exception {
        when(httpClient.chatCompletion(eq("key-one"), any()))
                .thenThrow(new GroqHttpException("rate limit", 429, true));
        when(httpClient.chatCompletion(eq("key-two"), any()))
                .thenReturn(new GroqHttpClient.GroqChatCompletionResult("Retry worked.", "stop", 8, 3, 11));

        ProviderResponse response = provider.generateReply(sampleRequest());

        assertThat(response.reply()).isEqualTo("Retry worked.");
        assertThat(response.providerMetadata()).containsEntry("keyIndex", "2");
        assertThat(response.providerMetadata()).containsEntry("retryCount", "1");
        verify(httpClient, times(1)).chatCompletion(eq("key-one"), any());
        verify(httpClient, times(1)).chatCompletion(eq("key-two"), any());
    }

    @Test
    void mapsAllKeyFailuresToProviderException() throws Exception {
        when(httpClient.chatCompletion(eq("key-one"), any()))
                .thenThrow(new GroqHttpException("rate limit", 429, true));
        when(httpClient.chatCompletion(eq("key-two"), any()))
                .thenThrow(new GroqHttpException("temporary", 503, true));

        assertThatThrownBy(() -> provider.generateReply(sampleRequest()))
                .isInstanceOf(ProviderException.class)
                .hasMessageContaining("temporarily unavailable");
    }

    @Test
    void healthReflectsLoadedKeysAndModel() {
        assertThat(provider.health()).isTrue();
        assertThat(provider.loadedKeyCount()).isEqualTo(2);
        assertThat(provider.providerInfo().model()).isEqualTo("llama-test");
    }

    private static ProviderRequest sampleRequest() {
        return new ProviderRequest(
                List.of(ProviderMessage.of(ProviderMessageRole.USER, "Hello", "USER_MESSAGE")),
                Map.of(),
                0.7,
                128,
                "llama-test");
    }
}
