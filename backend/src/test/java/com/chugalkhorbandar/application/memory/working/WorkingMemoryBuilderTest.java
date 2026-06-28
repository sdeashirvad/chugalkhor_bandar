package com.chugalkhorbandar.application.memory.working;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkingMemoryBuilderTest {

    private WorkingMemoryBuilder builder;
    private CurrentCharacter user;
    private ChatSession session;

    @BeforeEach
    void setUp() {
        WorkingMemoryProperties properties = new WorkingMemoryProperties();
        properties.setAnalysisWindowMessages(20);
        builder = new WorkingMemoryBuilder(properties);
        user = new CurrentCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", "Home Jungle", "Hippu Palace");
        session = new ChatSession("session-1", user, Instant.parse("2026-01-01T00:00:00Z"), Instant.parse("2026-01-01T00:00:00Z"), SessionStatus.ACTIVE);
    }

    @Test
    void buildIsDeterministicForSameInput() {
        Conversation conversation = conversationWithMessages(
                message("m1", Sender.USER, "Tell me about Hippu King and his palace."),
                message("m2", Sender.BANDAR, "Hippu King rules from Hippu Palace in Home Jungle."),
                message("m3", Sender.USER, "Where am I?"));

        WorkingMemorySnapshot first = builder.build(user, session, conversation, runtimeWorld(), 0L);
        WorkingMemorySnapshot second = builder.build(user, session, conversation, runtimeWorld(), 0L);

        assertThat(first.memory())
                .usingRecursiveComparison()
                .ignoringFields("lastUpdated")
                .isEqualTo(second.memory());
        assertThat(first.fieldTraces()).isEqualTo(second.fieldTraces());
    }

    @Test
    void repeatedTopicsPromoteHippuKingTopic() {
        Conversation conversation = conversationWithMessages(
                message("m1", Sender.USER, "What do you know about Hippu King?"),
                message("m2", Sender.BANDAR, "Hippu King is the ruler."),
                message("m3", Sender.USER, "Does Hippu King live in a palace?"),
                message("m4", Sender.BANDAR, "Yes, Hippu Palace."));

        WorkingMemorySnapshot snapshot = builder.build(user, session, conversation, runtimeWorld(), 0L);

        assertThat(snapshot.memory().activeTopic()).isEqualTo("Hippu King");
        assertThat(snapshot.memory().activeEntities()).contains("Hippu King");
    }

    @Test
    void tracksUnansweredQuestions() {
        Conversation conversation = conversationWithMessages(
                message("m1", Sender.USER, "Where am I?"),
                message("m2", Sender.BANDAR, "Hmm."),
                message("m3", Sender.USER, "Who rules Hippu Palace?"));

        WorkingMemorySnapshot snapshot = builder.build(user, session, conversation, runtimeWorld(), 0L);

        assertThat(snapshot.memory().unansweredQuestions()).contains("Where am I?", "Who rules Hippu Palace?");
    }

    @Test
    void extractsActiveEntitiesFromRecentWindow() {
        Conversation conversation = conversationWithMessages(
                message("m1", Sender.USER, "Tell me about Rabbitu Minister."),
                message("m2", Sender.BANDAR, "Rabbitu Minister serves the council."),
                message("m3", Sender.USER, "And Hippu King?"),
                message("m4", Sender.BANDAR, "Hippu King is the ruler."));

        WorkingMemorySnapshot snapshot = builder.build(user, session, conversation, runtimeWorld(), 0L);

        assertThat(snapshot.memory().activeEntities()).contains("Hippu King", "Rabbitu Minister");
    }

    @Test
    void detectsRequestedStoryNotYetStarted() {
        Conversation conversation = conversationWithMessages(message("m1", Sender.USER, "Please tell me a story about Hippu King."));

        WorkingMemorySnapshot snapshot = builder.build(user, session, conversation, runtimeWorld(), 0L);

        assertThat(snapshot.memory().currentStory()).isEqualTo("Story requested, not yet started");
    }

    @Test
    void incrementsVersionOnRebuild() {
        Conversation conversation = conversationWithMessages(message("m1", Sender.USER, "Hello"));

        WorkingMemorySnapshot first = builder.build(user, session, conversation, runtimeWorld(), 2L);
        WorkingMemorySnapshot second = builder.build(user, session, conversation, runtimeWorld(), first.memory().version());

        assertThat(first.memory().version()).isEqualTo(3L);
        assertThat(second.memory().version()).isEqualTo(4L);
    }

    private static RuntimeWorldContext runtimeWorld() {
        return new RuntimeWorldContext(
                "READY",
                "1.0",
                3,
                1,
                List.of("Hippu King", "Rabbitu Minister", "Hippu Palace", "Home Jungle"));
    }

    private static Conversation conversationWithMessages(ConversationMessage... messages) {
        return new Conversation(
                "conv-1",
                "session-1",
                new ConversationCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", "Home Jungle"),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z"),
                ConversationStatus.ACTIVE,
                List.of(messages));
    }

    private static ConversationMessage message(String id, Sender sender, String content) {
        return new ConversationMessage(
                id,
                "conv-1",
                sender,
                Instant.parse("2026-01-01T00:00:00Z"),
                content,
                Visibility.PUBLIC,
                Map.of());
    }
}
