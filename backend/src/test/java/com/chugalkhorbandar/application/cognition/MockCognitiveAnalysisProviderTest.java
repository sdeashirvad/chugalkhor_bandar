package com.chugalkhorbandar.application.cognition;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTrace;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanningTraceEntry;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MockCognitiveAnalysisProviderTest {

    private MockCognitiveAnalysisProvider provider;

    @BeforeEach
    void setUp() {
        provider = new MockCognitiveAnalysisProvider(new com.fasterxml.jackson.databind.ObjectMapper());
    }

    @Test
    void returnsDeterministicFixtureJson() {
        CognitiveAnalysisProviderResponse response = provider.analyzeConversation(input("remind me tomorrow"));

        assertThat(response.provider()).isEqualTo("mock");
        assertThat(response.rawJson()).contains("REMINDER");
        assertThat(response.rawJson()).contains("PROMOTE_TO_MEMORY");
    }

    @Test
    void mapsStoryRequestsToStorySeedObservation() {
        CognitiveAnalysisProviderResponse response = provider.analyzeConversation(input("tell me a story"));

        assertThat(response.rawJson()).contains("STORY_SEED");
        assertThat(response.rawJson()).contains("MERGE_ARTIFACT");
    }

    private static CognitiveAnalysisInput input(String message) {
        Instant now = Instant.parse("2026-06-01T12:00:00Z");
        CurrentCharacter user = new CurrentCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null);
        Conversation conversation = new Conversation(
                "conv-1",
                "session-1",
                new ConversationCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null),
                now,
                now,
                ConversationStatus.ACTIVE,
                List.of());
        ConversationMessage userMessage = new ConversationMessage(
                "m-1", "conv-1", Sender.USER, now, message, Visibility.PUBLIC, java.util.Map.of());
        ConversationPlanSnapshot plan = ConversationPlanSnapshot.planned(
                "session-1",
                new ConversationPlan(
                        ConversationGoal.STORY,
                        0.9,
                        true,
                        ConversationEnergy.HIGH,
                        ConversationArc.QUESTION_STORY,
                        1,
                        List.of(),
                        false,
                        true,
                        false,
                        false,
                        false,
                        "Warm",
                        ConversationOutcome.STORY_STARTED,
                        now,
                        false,
                        false,
                        null,
                        null),
                new ConversationPlanningTrace(List.of(new ConversationPlanningTraceEntry("story-request", "test"))));
        return new CognitiveAnalysisInput(
                user, conversation, List.of(userMessage), List.of(), null, null, plan, "character_alpha=Alpha");
    }
}
