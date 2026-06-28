package com.chugalkhorbandar.application.llm;

import com.chugalkhorbandar.config.LlmProperties;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MockLLMProvider implements LLMProvider {

    private static final String MODEL = "mock-bandar";
    private static final int MAX_CONTEXT_TOKENS = 8192;

    private final LlmProperties llmProperties;

    public MockLLMProvider(LlmProperties llmProperties) {
        this.llmProperties = llmProperties;
    }

    @Override
    public LLMProviderType providerType() {
        return LLMProviderType.MOCK;
    }

    @Override
    public ProviderResponse generateReply(ProviderRequest request) {
        long start = System.nanoTime();
        String reply = buildReply(request);
        long latencyMs = Math.max(1, (System.nanoTime() - start) / 1_000_000);
        int promptTokens = estimateTokens(request.messages().stream().map(ProviderMessage::content).toList());
        int completionTokens = estimateTokens(List.of(reply));

        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("provider", "mock");
        metadata.put("model", request.model());
        metadata.put("messageCount", String.valueOf(request.messages().size()));

        return new ProviderResponse(
                reply,
                new ProviderTokenUsage(promptTokens, completionTokens, promptTokens + completionTokens),
                Map.copyOf(metadata),
                latencyMs,
                "stop");
    }

    @Override
    public boolean health() {
        return true;
    }

    @Override
    public LLMProviderInfo providerInfo() {
        return new LLMProviderInfo(
                LLMProviderType.MOCK,
                "Mock Bandar",
                "Developer inspection provider — no AI generation",
                health(),
                MODEL);
    }

    @Override
    public ProviderCapabilities capabilities() {
        return new ProviderCapabilities(
                MAX_CONTEXT_TOKENS,
                llmProperties.getMaxOutputTokens(),
                true,
                true);
    }

    private static String buildReply(ProviderRequest request) {
        StringBuilder reply = new StringBuilder();
        reply.append("[Mock Bandar]\n\n");
        reply.append("I have received your request.\n\n");
        reply.append("Sections:\n\n");

        for (ProviderMessage message : request.messages()) {
            if (message.role() == ProviderMessageRole.SYSTEM && message.sectionType() != null) {
                String label = formatSectionLabel(message.sectionType());
                if (label != null) {
                    reply.append("- ").append(label).append('\n');
                }
            }
        }

        String userMessage = request.messages().stream()
                .filter(message -> message.role() == ProviderMessageRole.USER)
                .reduce((first, second) -> second)
                .map(ProviderMessage::content)
                .orElse("(none)");

        reply.append("\nUser asked:\n\n");
        reply.append('"').append(userMessage).append('"');

        return reply.toString();
    }

    private static String formatSectionLabel(String sectionType) {
        return switch (sectionType) {
            case "SYSTEM_IDENTITY", "CURRENT_USER" -> "Current User";
            case "RELATIONSHIP_TO_BANDAR" -> "Relationship to Bandar";
            case "PERSONALITY" -> "Personality";
            case "WORLD_FACTS" -> "World Facts";
            case "CURRENT_CHARACTER" -> "Current Character";
            case "CURRENT_LOCATION" -> "Current Location";
            case "RELATIONSHIPS" -> "Relationships";
            case "RELEVANT_STORIES" -> "Relevant Stories";
            case "CURRENT_CONVERSATION" -> "Conversation";
            case "SESSION_SUMMARY" -> "Session Summary";
            case "PUBLIC_EVENTS" -> "Public Events";
            case "LONG_TERM_MEMORY" -> "Long-Term Memory";
            case "SECRET_MEMORY" -> "Secret Memory";
            case "INSTRUCTIONS" -> "Instructions";
            case "CONVERSATION_STYLE" -> "Conversation Style";
            case "UNKNOWN" -> "Unknown";
            default -> null;
        };
    }

    private static int estimateTokens(List<String> parts) {
        int characters = parts.stream().mapToInt(String::length).sum();
        return characters == 0 ? 0 : Math.max(1, characters / 4);
    }
}
