package com.chugalkhorbandar.application.llm;

import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.LlmPromptPresenter;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PromptToProviderAdapter {

    private final LlmProperties llmProperties;

    public PromptToProviderAdapter(LlmProperties llmProperties) {
        this.llmProperties = llmProperties;
    }

    public ProviderRequest adapt(ComposedPrompt composedPrompt) {
        List<ProviderMessage> messages = new ArrayList<>();
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("sectionCount", String.valueOf(composedPrompt.sections().size()));
        metadata.put("estimatedPromptTokens", String.valueOf(composedPrompt.totalEstimatedTokens()));

        for (PromptSection section : composedPrompt.sections()) {
            appendSection(messages, section);
        }

        return new ProviderRequest(
                messages,
                Map.copyOf(metadata),
                llmProperties.getTemperature(),
                llmProperties.getMaxOutputTokens(),
                llmProperties.getModel());
    }

    private static void appendSection(List<ProviderMessage> messages, PromptSection section) {
        if (section.sectionType() == PromptSectionType.USER_MESSAGE) {
            messages.add(ProviderMessage.of(ProviderMessageRole.USER, section.content(), section.sectionType().name()));
            return;
        }
        if (section.sectionType() == PromptSectionType.CURRENT_CONVERSATION) {
            appendConversationHistory(messages, section);
            return;
        }
        messages.add(systemMessage(section));
    }

    private static void appendConversationHistory(List<ProviderMessage> messages, PromptSection section) {
        List<String> lines = section.content().lines().toList();
        boolean parsedTurn = false;
        for (String line : lines) {
            ProviderMessage turn = parseConversationLine(line);
            if (turn != null) {
                messages.add(turn);
                parsedTurn = true;
            }
        }
        if (!parsedTurn) {
            messages.add(systemMessage(section));
        }
    }

    private static ProviderMessage parseConversationLine(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("USER:")) {
            return ProviderMessage.of(
                    ProviderMessageRole.USER, trimmed.substring("USER:".length()).trim(), "CURRENT_CONVERSATION");
        }
        if (trimmed.startsWith("BANDAR:")) {
            return ProviderMessage.of(
                    ProviderMessageRole.ASSISTANT, trimmed.substring("BANDAR:".length()).trim(), "CURRENT_CONVERSATION");
        }
        if (trimmed.startsWith("SYSTEM:")) {
            return ProviderMessage.of(
                    ProviderMessageRole.SYSTEM, trimmed.substring("SYSTEM:".length()).trim(), "CURRENT_CONVERSATION");
        }
        return null;
    }

    private static ProviderMessage systemMessage(PromptSection section) {
        return ProviderMessage.of(
                ProviderMessageRole.SYSTEM, LlmPromptPresenter.format(section), section.sectionType().name());
    }
}
