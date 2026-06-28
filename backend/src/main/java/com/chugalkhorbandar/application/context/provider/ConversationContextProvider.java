package com.chugalkhorbandar.application.context.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionPriorities;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConversationContextProvider implements ContextProvider {

    @Override
    public String providerName() {
        return "conversationEngine";
    }

    @Override
    public Set<ContextSectionType> supportedTypes() {
        return Set.of(ContextSectionType.CURRENT_CONVERSATION, ContextSectionType.SESSION_SUMMARY);
    }

    @Override
    public List<ContextSection> plan(ContextPlannerRequest request, Set<ContextSectionType> selectedTypes) {
        List<ContextSection> sections = new ArrayList<>();
        Conversation conversation = request.currentConversation();
        if (selectedTypes.contains(ContextSectionType.CURRENT_CONVERSATION)) {
            String conversationId = conversation == null ? "none" : conversation.conversationId();
            sections.add(section(
                    ContextSectionType.CURRENT_CONVERSATION,
                    providerName(),
                    new ContextReference(providerName(), "conversation", conversationId, "window", priority(ContextSectionType.CURRENT_CONVERSATION))));
        }
        if (selectedTypes.contains(ContextSectionType.SESSION_SUMMARY)) {
            sections.add(section(
                    ContextSectionType.SESSION_SUMMARY,
                    "session",
                    new ContextReference("session", "session", request.session().sessionId(), "summary", priority(ContextSectionType.SESSION_SUMMARY))));
        }
        return sections;
    }

    @Override
    public boolean supports(ContextReference reference) {
        return Set.of("conversationEngine", "session").contains(reference.provider());
    }

    @Override
    public ResolvedContextSection resolve(ContextSection section, ContextPlannerRequest request) {
        if (section.type() == ContextSectionType.SESSION_SUMMARY) {
            return ResolvedContextSection.from(section, "Session summary is not available yet.");
        }
        Conversation conversation = request.currentConversation();
        if (conversation == null || "none".equals(section.reference().entityId())) {
            return ResolvedContextSection.from(section, "No active conversation.");
        }
        String content = conversation.messages().stream()
                .map(ConversationContextProvider::formatMessage)
                .collect(Collectors.joining("\n"));
        if (content.isBlank()) {
            content = "Conversation started. No messages yet.";
        }
        return ResolvedContextSection.from(section, content);
    }

    private static String formatMessage(ConversationMessage message) {
        return message.sender() + ": " + message.content();
    }

    private static int priority(ContextSectionType type) {
        return ContextSectionPriorities.priority(type);
    }
}
