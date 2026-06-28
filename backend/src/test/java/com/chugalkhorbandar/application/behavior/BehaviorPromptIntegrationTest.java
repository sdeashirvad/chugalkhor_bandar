package com.chugalkhorbandar.application.behavior;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationOutcome;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptComposeRequest;
import com.chugalkhorbandar.application.prompt.PromptComposer;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanContext;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class BehaviorPromptIntegrationTest {

    @Test
    void promptIncludesConversationStyleBeforeInstructions() {
        BehaviorContext behaviorContext = new BehaviorContext();
        PromptComposer composer = new PromptComposer(new ConversationPlanContext(), behaviorContext);
        behaviorContext.activate(new BehaviorProfile(
                OpeningStyle.OBSERVATION,
                NarrationStyle.STORY,
                HumorLevel.LIGHT,
                CuriosityLevel.HIGH,
                EndingStyle.QUESTION,
                ConversationFlavor.NOSTALGIC,
                EnergyModifier.LIVELY,
                StorytellingPreference.STRONG,
                Instant.now()));
        try {
            ComposedPrompt composed = composer.compose(new PromptComposeRequest(
                    new ResolvedContext(List.of(), List.of(), 0),
                    "Tell me a story",
                    new CurrentCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                    new ChatSession(
                            "session-1",
                            new CurrentCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                            Instant.now(),
                            Instant.now(),
                            SessionStatus.ACTIVE),
                    new Conversation(
                            "conv-1",
                            "session-1",
                            new ConversationCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null),
                            Instant.now(),
                            Instant.now(),
                            ConversationStatus.ACTIVE,
                            List.of())));

            List<PromptSectionType> order =
                    composed.sections().stream().map(section -> section.sectionType()).toList();
            assertThat(order.indexOf(PromptSectionType.CONVERSATION_STYLE))
                    .isGreaterThan(order.indexOf(PromptSectionType.USER_MESSAGE));
            assertThat(order.indexOf(PromptSectionType.CONVERSATION_STYLE))
                    .isLessThan(order.indexOf(PromptSectionType.INSTRUCTIONS));

            String style = composed.sections().stream()
                    .filter(section -> section.sectionType() == PromptSectionType.CONVERSATION_STYLE)
                    .findFirst()
                    .orElseThrow()
                    .content();
            assertThat(style).contains("Today you feel");
            assertThat(style).contains("storytelling");
        } finally {
            behaviorContext.clear();
        }
    }

    @Test
    void promptOmitsConversationStyleWhenBehaviorInactive() {
        PromptComposer composer = new PromptComposer(new ConversationPlanContext(), new BehaviorContext());
        ComposedPrompt composed = composer.compose(new PromptComposeRequest(
                new ResolvedContext(List.of(), List.of(), 0),
                "Hello",
                new CurrentCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                new ChatSession(
                        "session-1",
                        new CurrentCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                        Instant.now(),
                        Instant.now(),
                        SessionStatus.ACTIVE),
                null));

        assertThat(composed.sections()).noneMatch(section -> section.sectionType() == PromptSectionType.CONVERSATION_STYLE);
    }
}
