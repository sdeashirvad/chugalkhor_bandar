package com.chugalkhorbandar.adapters.persistence.memory;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryConversationPersistenceTest {

    @Test
    void persistsConversationAndMessages() {
        InMemoryConversationStore store = new InMemoryConversationStore();
        InMemoryConversationRepository conversations = new InMemoryConversationRepository(store);
        InMemoryConversationMessageRepository messages = new InMemoryConversationMessageRepository(store);

        String conversationId = UUID.randomUUID().toString();
        Conversation conversation = new Conversation(
                conversationId,
                "session-1",
                new ConversationCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null),
                Instant.parse("2026-06-27T12:00:00Z"),
                Instant.parse("2026-06-27T12:00:00Z"),
                ConversationStatus.ACTIVE,
                List.of());
        conversations.save(conversation);

        messages.append(new ConversationMessage(
                UUID.randomUUID().toString(),
                conversationId,
                Sender.USER,
                Instant.parse("2026-06-27T12:01:00Z"),
                "Hello",
                Visibility.PUBLIC,
                Map.of()));
        messages.append(new ConversationMessage(
                UUID.randomUUID().toString(),
                conversationId,
                Sender.BANDAR,
                Instant.parse("2026-06-27T12:01:01Z"),
                "I heard you.",
                Visibility.PUBLIC,
                Map.of()));

        Conversation loaded = conversations.findActiveBySessionId("session-1").orElseThrow();

        assertThat(loaded.messages()).hasSize(2);
        assertThat(loaded.messages().get(0).content()).isEqualTo("Hello");
        assertThat(loaded.messages().get(1).sender()).isEqualTo(Sender.BANDAR);
    }
}
