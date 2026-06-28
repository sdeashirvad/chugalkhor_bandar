package com.chugalkhorbandar.application.context.knowledge.provider;

import com.chugalkhorbandar.application.conversation.ConversationWindowBuilder;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryProperties;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPriorities;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConversationKnowledgeProvider implements KnowledgeProvider {

    private static final Set<KnowledgeFragmentType> SUPPORTED = Set.of(KnowledgeFragmentType.CONVERSATION);

    private final WorkingMemoryProperties workingMemoryProperties;

    public ConversationKnowledgeProvider(WorkingMemoryProperties workingMemoryProperties) {
        this.workingMemoryProperties = workingMemoryProperties;
    }

    @Override
    public String providerName() {
        return "conversationKnowledge";
    }

    @Override
    public Set<KnowledgeFragmentType> supportedFragmentTypes() {
        return SUPPORTED;
    }

    @Override
    public List<KnowledgeFragmentRequest> plan(ContextPlannerRequest request, Set<KnowledgeFragmentType> selectedTypes) {
        if (!selectedTypes.contains(KnowledgeFragmentType.CONVERSATION)) {
            return List.of();
        }
        Conversation conversation = request.currentConversation();
        String conversationId = conversation == null ? "none" : conversation.conversationId();
        return List.of(new KnowledgeFragmentRequest(
                KnowledgeFragmentType.CONVERSATION,
                "Always included",
                KnowledgeFragmentPriorities.priority(KnowledgeFragmentType.CONVERSATION),
                new ContextReference(
                        providerName(),
                        "conversation",
                        conversationId,
                        "window",
                        KnowledgeFragmentPriorities.priority(KnowledgeFragmentType.CONVERSATION))));
    }

    @Override
    public Optional<KnowledgeFragment> resolve(KnowledgeFragmentRequest request, ContextPlannerRequest context) {
        Conversation conversation = context.currentConversation();
        if (conversation == null || "none".equals(request.reference().entityId())) {
            return Optional.of(KnowledgeFragment.of(
                    KnowledgeFragmentType.CONVERSATION,
                    "Current Conversation",
                    "No active conversation.",
                    "conversation",
                    "window",
                    Set.of("conversation"),
                    1.0));
        }
        String content = ConversationWindowBuilder.build(
                        conversation, workingMemoryProperties.getConversationWindowMessages())
                .messages()
                .stream()
                .map(ConversationKnowledgeProvider::formatMessage)
                .collect(Collectors.joining("\n"));
        if (content.isBlank()) {
            content = "Conversation started. No messages yet.";
        }
        return Optional.of(KnowledgeFragment.of(
                KnowledgeFragmentType.CONVERSATION,
                "Current Conversation",
                content,
                conversation.conversationId(),
                "window",
                Set.of("conversation"),
                1.0));
    }

    private static String formatMessage(ConversationMessage message) {
        return message.sender() + ": " + message.content();
    }
}
