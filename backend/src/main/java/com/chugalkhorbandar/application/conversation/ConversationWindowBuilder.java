package com.chugalkhorbandar.application.conversation;

import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationWindow;
import com.chugalkhorbandar.domain.conversation.Sender;
import java.util.List;

public final class ConversationWindowBuilder {

    private static final int DEFAULT_MAX_MESSAGES = 10;

    private ConversationWindowBuilder() {}

    public static ConversationWindow build(Conversation conversation) {
        return build(conversation, DEFAULT_MAX_MESSAGES);
    }

    public static ConversationWindow build(Conversation conversation, int maxMessages) {
        List<ConversationMessage> allMessages = conversation.messages();
        int limit = Math.max(1, maxMessages);
        List<ConversationMessage> windowMessages = allMessages.size() <= limit
                ? List.copyOf(allMessages)
                : List.copyOf(allMessages.subList(allMessages.size() - limit, allMessages.size()));
        return new ConversationWindow(windowMessages, determineCurrentTurn(allMessages), estimateTokens(windowMessages));
    }

    private static Sender determineCurrentTurn(List<ConversationMessage> messages) {
        if (messages.isEmpty()) {
            return Sender.USER;
        }
        return switch (messages.get(messages.size() - 1).sender()) {
            case USER -> Sender.BANDAR;
            case BANDAR, SYSTEM -> Sender.USER;
        };
    }

    private static int estimateTokens(List<ConversationMessage> messages) {
        int characters = messages.stream().mapToInt(message -> message.content().length()).sum();
        return characters == 0 ? 0 : Math.max(1, characters / 4);
    }
}
