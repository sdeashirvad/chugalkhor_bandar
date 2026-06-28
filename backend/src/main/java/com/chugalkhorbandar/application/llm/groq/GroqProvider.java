package com.chugalkhorbandar.application.llm.groq;

import com.chugalkhorbandar.application.llm.LLMProvider;
import com.chugalkhorbandar.application.llm.LLMProviderInfo;
import com.chugalkhorbandar.application.llm.LLMProviderType;
import com.chugalkhorbandar.application.llm.ProviderCapabilities;
import com.chugalkhorbandar.application.llm.ProviderException;
import com.chugalkhorbandar.application.llm.ProviderMessage;
import com.chugalkhorbandar.application.llm.ProviderMessageRole;
import com.chugalkhorbandar.application.llm.ProviderRequest;
import com.chugalkhorbandar.application.llm.ProviderResponse;
import com.chugalkhorbandar.application.llm.ProviderTokenUsage;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GroqProvider implements LLMProvider {

    private static final int MAX_CONTEXT_TOKENS = 131072;

    private final GroqKeyPool keyPool;
    private final GroqHttpClient httpClient;
    private final LlmProperties llmProperties;

    public GroqProvider(
            GroqKeyPool keyPool, @Qualifier("groqHttpClient") GroqHttpClient httpClient, LlmProperties llmProperties) {
        this.keyPool = keyPool;
        this.httpClient = httpClient;
        this.llmProperties = llmProperties;
    }

    @Override
    public LLMProviderType providerType() {
        return LLMProviderType.GROQ;
    }

    @Override
    public ProviderResponse generateReply(ProviderRequest request) {
        if (keyPool.keyCount() == 0) {
            throw new ProviderException("Groq provider is not configured with any API keys.");
        }

        GroqKeyPool.GroqKeySelection primaryKey = keyPool.acquireKey();
        try {
            return executeRequest(request, primaryKey, 0);
        } catch (GroqHttpException primaryFailure) {
            if (!primaryFailure.retryable()) {
                throw toProviderException(primaryFailure);
            }
            Optional<GroqKeyPool.GroqKeySelection> alternateKey = keyPool.alternateKey(primaryKey.displayIndex());
            if (alternateKey.isEmpty()) {
                throw toProviderException(primaryFailure);
            }
            try {
                return executeRequest(request, alternateKey.orElseThrow(), 1);
            } catch (GroqHttpException retryFailure) {
                throw toProviderException(retryFailure);
            }
        }
    }

    private ProviderResponse executeRequest(
            ProviderRequest request, GroqKeyPool.GroqKeySelection keySelection, int retryCount)
            throws GroqHttpException {
        long start = System.nanoTime();
        GroqHttpClient.GroqChatCompletionResult result = httpClient.chatCompletion(
                keySelection.apiKey(), toGroqRequest(request));
        long latencyMs = Math.max(1, (System.nanoTime() - start) / 1_000_000);

        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("provider", "groq");
        metadata.put("model", resolveModel(request));
        metadata.put("keyIndex", String.valueOf(keySelection.displayIndex()));
        metadata.put("retryCount", String.valueOf(retryCount));
        metadata.put("loadedKeyCount", String.valueOf(keyPool.keyCount()));

        return new ProviderResponse(
                result.reply(),
                new ProviderTokenUsage(result.promptTokens(), result.completionTokens(), result.totalTokens()),
                Map.copyOf(metadata),
                latencyMs,
                result.finishReason());
    }

    @Override
    public boolean health() {
        return keyPool.keyCount() > 0;
    }

    @Override
    public LLMProviderInfo providerInfo() {
        return new LLMProviderInfo(
                LLMProviderType.GROQ,
                "Groq",
                "Groq OpenAI-compatible chat completions (" + keyPool.keyCount() + " key(s) loaded)",
                health(),
                resolveModel(null));
    }

    @Override
    public ProviderCapabilities capabilities() {
        return new ProviderCapabilities(
                MAX_CONTEXT_TOKENS,
                llmProperties.getMaxOutputTokens(),
                true,
                true);
    }

    public int loadedKeyCount() {
        return keyPool.keyCount();
    }

    private GroqHttpClient.GroqChatCompletionRequest toGroqRequest(ProviderRequest request) {
        List<GroqHttpClient.GroqMessage> messages = request.messages().stream()
                .map(this::toGroqMessage)
                .toList();
        return new GroqHttpClient.GroqChatCompletionRequest(
                resolveModel(request),
                messages,
                request.temperature(),
                request.maxOutputTokens());
    }

    private GroqHttpClient.GroqMessage toGroqMessage(ProviderMessage message) {
        String role = switch (message.role()) {
            case SYSTEM -> "system";
            case USER -> "user";
            case ASSISTANT -> "assistant";
        };
        return new GroqHttpClient.GroqMessage(role, message.content());
    }

    private String resolveModel(ProviderRequest request) {
        if (request != null && request.model() != null && !request.model().isBlank() && !request.model().startsWith("mock")) {
            return request.model();
        }
        if (llmProperties.getModel() != null && !llmProperties.getModel().isBlank()) {
            return llmProperties.getModel();
        }
        return "llama-3.3-70b-versatile";
    }

    private static ProviderException toProviderException(GroqHttpException exception) {
        return new ProviderException(
                "The language model provider is temporarily unavailable. Please try again.",
                exception);
    }
}
