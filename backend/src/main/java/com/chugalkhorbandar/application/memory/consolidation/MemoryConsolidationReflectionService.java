package com.chugalkhorbandar.application.memory.consolidation;

import com.chugalkhorbandar.application.llm.groq.GroqHttpClient;
import com.chugalkhorbandar.application.llm.groq.GroqKeyPool;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MemoryConsolidationReflectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryConsolidationReflectionService.class);

    private final MemoryConsolidationProperties properties;
    private final LlmProperties llmProperties;
    private final GroqKeyPool groqKeyPool;
    private final GroqHttpClient groqHttpClient;

    public MemoryConsolidationReflectionService(
            MemoryConsolidationProperties properties,
            LlmProperties llmProperties,
            GroqKeyPool groqKeyPool,
            @Qualifier("cognitiveAnalysisGroqHttpClient") GroqHttpClient groqHttpClient) {
        this.properties = properties;
        this.llmProperties = llmProperties;
        this.groqKeyPool = groqKeyPool;
        this.groqHttpClient = groqHttpClient;
    }

    public String generateReflection(MemoryConsolidationResult result, MemoryConsolidationDailyStats stats) {
        if (!properties.isReflectionEnabled()) {
            return "";
        }
        try {
            String apiKey = groqKeyPool.keyCount() > 0 ? groqKeyPool.acquireKey().apiKey() : "";
            if (apiKey.isBlank() || "mock".equalsIgnoreCase(llmProperties.getProvider())) {
                return mockReflection(stats);
            }
            String prompt = """
                    You are Bandar, the ancient monkey sage of the jungle.
                    Write a short nightly reflection (3-4 sentences) about today's memory consolidation.
                    Promoted: %d. Discarded: %d. Candidates: %d. Pending promises: %d.
                    Be warm, wise, and concise. No bullet points.
                    """
                    .formatted(stats.promoted(), stats.discarded(), stats.candidates(), stats.pendingPromises());
            GroqHttpClient.GroqChatCompletionResult completion = groqHttpClient.chatCompletion(
                    apiKey,
                    new GroqHttpClient.GroqChatCompletionRequest(
                            llmProperties.getModel(),
                            List.of(new GroqHttpClient.GroqMessage("user", prompt)),
                            0.7,
                            256));
            return completion.reply().trim();
        } catch (Exception exception) {
            LOGGER.warn("Bandar reflection failed; continuing consolidation", exception);
            return "";
        }
    }

    private static String mockReflection(MemoryConsolidationDailyStats stats) {
        return "Today I heard many stories. Some deserved to be remembered — " + stats.promoted()
                + " found their place. Others were simply pleasant moments — " + stats.discarded()
                + " I set aside. Tomorrow brings new conversations.";
    }
}
