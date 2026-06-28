package com.chugalkhorbandar.application.conversation.director;

import static org.assertj.core.api.Assertions.assertThat;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptComposeRequest;
import com.chugalkhorbandar.application.prompt.PromptComposer;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConversationPlanInstructionIntegrationTest {

    @Test
    void promptIncludesDirectorInstructionWhenPlanActive() {
        ConversationPlanContext context = new ConversationPlanContext();
        PromptComposer composer = new PromptComposer(context, new com.chugalkhorbandar.application.behavior.BehaviorContext());
        ConversationPlan plan = new ConversationPlan(
                ConversationGoal.STORY,
                0.94,
                true,
                ConversationEnergy.HIGH,
                ConversationArc.QUESTION_STORY,
                2,
                List.of(0L),
                false,
                true,
                false,
                false,
                false,
                "Narrative",
                ConversationOutcome.STORY_STARTED,
                Instant.parse("2026-01-01T00:00:00Z"),
                false,
                false,
                null,
                null);

        context.activate(plan, 0);
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

            String instructions = composed.sections().stream()
                    .filter(section -> section.sectionType() == PromptSectionType.INSTRUCTIONS)
                    .findFirst()
                    .orElseThrow()
                    .content();

            assertThat(instructions).contains("Conversation Goal");
            assertThat(instructions).contains("Tell a story from memory.");
            assertThat(instructions).contains("reply 1 of 2");
        } finally {
            context.clear();
        }
    }

    @Test
    void instructionBuilderCoversGoodbyeGoal() {
        String instruction = ConversationPlanInstructionBuilder.build(
                new ConversationPlan(
                        ConversationGoal.GOODBYE,
                        0.95,
                        false,
                        ConversationEnergy.LOW,
                        ConversationArc.GOODBYE,
                        1,
                        List.of(),
                        false,
                        false,
                        false,
                        false,
                        true,
                        "Warm",
                        ConversationOutcome.RESOLVED,
                        Instant.now(),
                        false,
                        false,
                        null,
                        null),
                0,
                1);

        assertThat(instruction).contains("Do not extend the conversation");
    }
}
