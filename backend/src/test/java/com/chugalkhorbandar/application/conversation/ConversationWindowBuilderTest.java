package com.chugalkhorbandar.application.conversation;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.ConversationWindow;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConversationWindowBuilderTest {

    @Test
    void buildsWindowWithCurrentTurnAndTokenEstimate() {
        String conversationId = UUID.randomUUID().toString();
        List<ConversationMessage> messages = new ArrayList<>();
        messages.add(message(conversationId, Sender.USER, "Hello there"));
        messages.add(message(conversationId, Sender.BANDAR, "I heard you."));

        Conversation conversation = new Conversation(
                conversationId,
                "session-1",
                new ConversationCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null),
                Instant.parse("2026-06-27T12:00:00Z"),
                Instant.parse("2026-06-27T12:00:00Z"),
                ConversationStatus.ACTIVE,
                messages);

        ConversationWindow window = ConversationWindowBuilder.build(conversation, 10);

        assertThat(window.messages()).hasSize(2);
        assertThat(window.currentTurn()).isEqualTo(Sender.USER);
        assertThat(window.tokenEstimate()).isEqualTo(5);
    }

    private static ConversationMessage message(String conversationId, Sender sender, String content) {
        return new ConversationMessage(
                UUID.randomUUID().toString(),
                conversationId,
                sender,
                Instant.parse("2026-06-27T12:00:00Z"),
                content,
                Visibility.PUBLIC,
                Map.of());
    }
}
