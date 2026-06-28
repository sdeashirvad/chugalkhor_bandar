package com.chugalkhorbandar.application.cognition;

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
import java.util.Map;

final class MockCognitiveAnalysisProviderTestSupport {

    private MockCognitiveAnalysisProviderTestSupport() {}

    static CognitiveAnalysisInput sampleInput() {
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
                "m-1", "conv-1", Sender.USER, now, "tell me a story", Visibility.PUBLIC, Map.of());
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
                user, conversation, List.of(userMessage), List.of(), null, null, plan, "status=READY");
    }
}
