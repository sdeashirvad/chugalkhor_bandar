package com.chugalkhorbandar.domain.conversation.ports;

import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import java.util.List;

public interface ConversationMessageRepository {

    ConversationMessage append(ConversationMessage message);

    List<ConversationMessage> findByConversationIdOrdered(String conversationId);
}
