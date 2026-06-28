package com.chugalkhorbandar.application.cognition;

import com.chugalkhorbandar.application.llm.ProviderException;
import com.chugalkhorbandar.application.llm.groq.GroqHttpClient;
import com.chugalkhorbandar.application.llm.groq.GroqHttpException;
import com.chugalkhorbandar.application.llm.groq.GroqKeyPool;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GroqCognitiveAnalysisProvider implements CognitiveAnalysisProvider {

    private final GroqKeyPool keyPool;
    private final GroqHttpClient httpClient;
    private final CognitiveAnalysisProperties properties;
    private final LlmProperties llmProperties;

    public GroqCognitiveAnalysisProvider(
            GroqKeyPool keyPool,
            @Qualifier("cognitiveAnalysisGroqHttpClient") GroqHttpClient httpClient,
            CognitiveAnalysisProperties properties,
            LlmProperties llmProperties) {
        this.keyPool = keyPool;
        this.httpClient = httpClient;
        this.properties = properties;
        this.llmProperties = llmProperties;
    }

    @Override
    public String providerName() {
        return "groq";
    }

    @Override
    public boolean isAvailable() {
        return keyPool.keyCount() > 0;
    }

    @Override
    public CognitiveAnalysisProviderResponse analyzeConversation(CognitiveAnalysisInput input) {
        if (!isAvailable()) {
            throw new ProviderException("Groq cognitive analysis provider is not configured with any API keys.");
        }

        GroqKeyPool.GroqKeySelection primaryKey = keyPool.acquireKey();
        try {
            return execute(input, primaryKey, 0);
        } catch (GroqHttpException primaryFailure) {
            if (!primaryFailure.retryable()) {
                throw toProviderException(primaryFailure);
            }
            Optional<GroqKeyPool.GroqKeySelection> alternateKey = keyPool.alternateKey(primaryKey.displayIndex());
            if (alternateKey.isEmpty()) {
                throw toProviderException(primaryFailure);
            }
            try {
                return execute(input, alternateKey.orElseThrow(), 1);
            } catch (GroqHttpException retryFailure) {
                throw toProviderException(retryFailure);
            }
        }
    }

    private CognitiveAnalysisProviderResponse execute(
            CognitiveAnalysisInput input, GroqKeyPool.GroqKeySelection keySelection, int retryCount)
            throws GroqHttpException {
        long start = System.nanoTime();
        GroqHttpClient.GroqChatCompletionRequest request = new GroqHttpClient.GroqChatCompletionRequest(
                resolveModel(),
                List.of(
                        new GroqHttpClient.GroqMessage("system", CognitiveAnalysisPromptBuilder.systemPrompt()),
                        new GroqHttpClient.GroqMessage(
                                "user", CognitiveAnalysisPromptBuilder.buildUserPayload(input))),
                properties.getTemperature(),
                llmProperties.getMaxOutputTokens());
        GroqHttpClient.GroqChatCompletionResult result = httpClient.chatCompletion(keySelection.apiKey(), request);
        long latencyMs = Math.max(1, (System.nanoTime() - start) / 1_000_000);
        return new CognitiveAnalysisProviderResponse(providerName(), resolveModel(), latencyMs, result.reply());
    }

    private String resolveModel() {
        if (llmProperties.getModel() != null
                && !llmProperties.getModel().isBlank()
                && !llmProperties.getModel().startsWith("mock")) {
            return llmProperties.getModel();
        }
        return "llama-3.3-70b-versatile";
    }

    private static ProviderException toProviderException(GroqHttpException exception) {
        return new ProviderException("Cognitive analysis provider is temporarily unavailable.", exception);
    }
}
