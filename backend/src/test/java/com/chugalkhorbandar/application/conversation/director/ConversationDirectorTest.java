package com.chugalkhorbandar.application.conversation.director;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationWindow;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationDirectorTest {

    private ConversationDirector director;

    @BeforeEach
    void setUp() {
        ConversationDirectorProperties properties = new ConversationDirectorProperties();
        properties.getMessageDelayRange().setSecondMessageMinMs(0);
        properties.getMessageDelayRange().setSecondMessageMaxMs(0);
        properties.getMessageDelayRange().setThirdMessageMinMs(0);
        properties.getMessageDelayRange().setThirdMessageMaxMs(0);
        properties.setDevDelayMultiplier(0);
        director = new ConversationDirector(properties);
    }

    @Test
    void selectsStoryGoalForStoryRequest() {
        ConversationPlanSnapshot snapshot = director.plan("session-1", input("Please tell me a story about Hippu King", window()));

        assertThat(snapshot.plan().goal()).isEqualTo(ConversationGoal.STORY);
        assertThat(snapshot.plan().conversationEnergy()).isEqualTo(ConversationEnergy.HIGH);
        assertThat(snapshot.plan().conversationArc()).isEqualTo(ConversationArc.QUESTION_STORY);
        assertThat(snapshot.plan().continueConversation()).isTrue();
        assertThat(snapshot.plan().expectedMessageCount()).isBetween(2, 3);
        assertThat(snapshot.plan().tellStory()).isTrue();
        assertThat(snapshot.plan().outcome()).isEqualTo(ConversationOutcome.STORY_STARTED);
        assertThat(snapshot.trace().entries()).extracting(ConversationPlanningTraceEntry::rule).contains("story-request");
    }

    @Test
    void selectsGoodbyeGoalForThankYou() {
        ConversationPlanSnapshot snapshot = director.plan("session-1", input("Thank you", window(user("Thanks"), bandar("Anytime"))));

        assertThat(snapshot.plan().goal()).isEqualTo(ConversationGoal.GOODBYE);
        assertThat(snapshot.plan().conversationEnergy()).isEqualTo(ConversationEnergy.LOW);
        assertThat(snapshot.plan().conversationArc()).isEqualTo(ConversationArc.GOODBYE);
        assertThat(snapshot.plan().continueConversation()).isFalse();
        assertThat(snapshot.plan().expectedMessageCount()).isEqualTo(1);
        assertThat(snapshot.plan().askFollowUpQuestion()).isFalse();
        assertThat(snapshot.plan().endConversation()).isTrue();
        assertThat(snapshot.plan().outcome()).isEqualTo(ConversationOutcome.RESOLVED);
    }

    @Test
    void selectsLocationHelpForWhereAmI() {
        ConversationPlanSnapshot snapshot = director.plan("session-1", input("Where am I?", window()));

        assertThat(snapshot.plan().goal()).isEqualTo(ConversationGoal.LOCATION_HELP);
        assertThat(snapshot.plan().conversationEnergy()).isEqualTo(ConversationEnergy.LOW);
        assertThat(snapshot.plan().expectedMessageCount()).isEqualTo(1);
    }

    @Test
    void selectsCheerUpForBoredom() {
        ConversationPlanSnapshot snapshot = director.plan("session-1", input("I'm bored", window()));

        assertThat(snapshot.plan().goal()).isEqualTo(ConversationGoal.CHEER_UP);
        assertThat(snapshot.plan().conversationEnergy()).isEqualTo(ConversationEnergy.HIGH);
        assertThat(snapshot.plan().conversationArc()).isEqualTo(ConversationArc.CHEER_UP);
        assertThat(snapshot.plan().continueConversation()).isTrue();
        assertThat(snapshot.plan().tellStory()).isTrue();
        assertThat(snapshot.plan().expectedMessageCount()).isBetween(2, 3);
    }

    @Test
    void planningIsDeterministic() {
        ConversationDirectorInput input = input("Where am I?", window());
        ConversationPlan first = director.plan("session-1", input).plan();
        ConversationPlan second = director.plan("session-1", input).plan();

        assertThat(first.goal()).isEqualTo(second.goal());
        assertThat(first.expectedMessageCount()).isEqualTo(second.expectedMessageCount());
        assertThat(first.conversationEnergy()).isEqualTo(second.conversationEnergy());
        assertThat(first.outcome()).isEqualTo(second.outcome());
        assertThat(first.confidence()).isEqualTo(second.confidence());
    }

    @Test
    void assignsConfidenceByRuleStrength() {
        ConversationPlan goodbye = director.plan("session-1", input("Thank you", window())).plan();
        ConversationPlan smallTalk = director.plan("session-1", input("Hmm", window(user("Hmm"), bandar("Indeed")))).plan();

        assertThat(goodbye.confidence()).isGreaterThan(smallTalk.confidence());
    }

    @Test
    void continueStoryWhenWorkingMemoryIndicatesProgress() {
        WorkingMemory memory = new WorkingMemory(
                "session-1",
                "Storytelling",
                "Narrative",
                "Story in progress",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                Instant.parse("2026-01-01T00:00:00Z"),
                1L);
        ConversationDirectorInput input = new ConversationDirectorInput(
                user(), memory, window(user("Continue"), bandar("Once upon a time")), "Continue");

        ConversationPlanSnapshot snapshot = director.plan("session-1", input);

        assertThat(snapshot.plan().goal()).isEqualTo(ConversationGoal.CONTINUE_STORY);
        assertThat(snapshot.plan().conversationArc()).isEqualTo(ConversationArc.STORY_CONTINUATION);
        assertThat(snapshot.plan().outcome()).isEqualTo(ConversationOutcome.STORY_COMPLETED);
    }

    @Test
    void longStoryRequestUsesVeryHighEnergy() {
        ConversationPlanSnapshot snapshot =
                director.plan("session-1", input("Tell me a long story about the jungle", window()));

        assertThat(snapshot.plan().conversationEnergy()).isEqualTo(ConversationEnergy.VERY_HIGH);
        assertThat(snapshot.plan().expectedMessageCount()).isEqualTo(3);
    }

    private static ConversationDirectorInput input(String latestMessage, ConversationWindow window) {
        return new ConversationDirectorInput(user(), null, window, latestMessage);
    }

    private static CurrentCharacter user() {
        return new CurrentCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", "Home Jungle", "Hippu Palace");
    }

    private static ConversationWindow window(ConversationMessage... messages) {
        return new ConversationWindow(List.of(messages), Sender.BANDAR, 10);
    }

    private static ConversationMessage user(String content) {
        return message(Sender.USER, content);
    }

    private static ConversationMessage bandar(String content) {
        return message(Sender.BANDAR, content);
    }

    private static ConversationMessage message(Sender sender, String content) {
        return new ConversationMessage(
                "m-" + content.hashCode(),
                "conv-1",
                sender,
                Instant.parse("2026-01-01T00:00:00Z"),
                content,
                Visibility.PUBLIC,
                Map.of());
    }
}
