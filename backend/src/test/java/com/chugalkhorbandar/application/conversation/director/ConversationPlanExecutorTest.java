package com.chugalkhorbandar.application.conversation.director;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.application.behavior.BehaviorContext;
import com.chugalkhorbandar.application.behavior.BehaviorEngineService;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationMessageRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryConversationStore;
import com.chugalkhorbandar.application.llm.LLMGenerateResult;
import com.chugalkhorbandar.application.llm.LLMProviderInfo;
import com.chugalkhorbandar.application.llm.LLMProviderType;
import com.chugalkhorbandar.application.llm.LLMService;
import com.chugalkhorbandar.application.llm.ProviderResponse;
import com.chugalkhorbandar.application.llm.ProviderTokenUsage;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationPlanExecutorTest {

    @Mock
    private LLMService llmService;

    @Mock
    private ConversationDirectorService directorService;

    @Mock
    private BehaviorEngineService behaviorEngineService;

    private ConversationPlanContext planContext;
    private BehaviorContext behaviorContext;
    private ConversationExecutionRegistry executionRegistry;
    private InMemoryConversationStore conversationStore;
    private ConversationPlanExecutor executor;

    @BeforeEach
    void setUp() {
        planContext = new ConversationPlanContext();
        behaviorContext = new BehaviorContext();
        executionRegistry = new ConversationExecutionRegistry();
        conversationStore = new InMemoryConversationStore();
        executor = new ConversationPlanExecutor(
                llmService,
                planContext,
                directorService,
                new InMemoryConversationMessageRepository(conversationStore),
                executionRegistry,
                behaviorEngineService,
                behaviorContext);
    }

    @Test
    void executesMultiMessagePlanWithDelays() {
        ConversationPlan plan = plan(
                ConversationGoal.STORY,
                ConversationEnergy.HIGH,
                ConversationArc.QUESTION_STORY,
                3,
                List.of(10L, 20L));
        ConversationPlanSnapshot snapshot = ConversationPlanSnapshot.planned(
                "session-1", plan, new ConversationPlanningTrace(List.of()));
        Conversation conversation = conversation();

        when(llmService.generate(eq("session-1"), eq("Tell me a story")))
                .thenReturn(llmResult("Part 1"))
                .thenReturn(llmResult("Part 2"))
                .thenReturn(llmResult("Part 3"));

        long started = System.currentTimeMillis();
        List<ConversationMessage> replies = executor.execute(snapshot, "session-1", "Tell me a story", conversation);
        long elapsed = System.currentTimeMillis() - started;

        assertThat(replies).hasSize(3);
        assertThat(replies).extracting(ConversationMessage::content).containsExactly("Part 1", "Part 2", "Part 3");
        assertThat(elapsed).isGreaterThanOrEqualTo(30L);
        verify(llmService, times(3)).generate(eq("session-1"), eq("Tell me a story"));
        verify(directorService).markExecutionFinished(eq("session-1"), eq(plan.createdAt()), any());
        assertThat(planContext.current()).isEmpty();
    }

    @Test
    void executesSingleMessageGoodbyePlan() {
        ConversationPlan plan = plan(
                ConversationGoal.GOODBYE,
                ConversationEnergy.LOW,
                ConversationArc.GOODBYE,
                1,
                List.of());
        ConversationPlanSnapshot snapshot = ConversationPlanSnapshot.planned(
                "session-1", plan, new ConversationPlanningTrace(List.of()));

        when(llmService.generate(eq("session-1"), eq("Thank you"))).thenReturn(llmResult("Farewell"));

        List<ConversationMessage> replies = executor.execute(snapshot, "session-1", "Thank you", conversation());

        assertThat(replies).hasSize(1);
        assertThat(replies.get(0).sender()).isEqualTo(Sender.BANDAR);
        verify(directorService).markExecutionFinished(eq("session-1"), eq(plan.createdAt()), any());
    }

    @Test
    void activatesPlanContextDuringGeneration() {
        ConversationPlan plan = plan(
                ConversationGoal.QUESTION,
                ConversationEnergy.MEDIUM,
                ConversationArc.QUESTION_ANSWER,
                2,
                List.of(0L));
        ConversationPlanSnapshot snapshot = ConversationPlanSnapshot.planned(
                "session-1", plan, new ConversationPlanningTrace(List.of()));

        when(llmService.generate(eq("session-1"), eq("Why?"))).thenAnswer(invocation -> {
            assertThat(planContext.current()).isPresent();
            assertThat(planContext.current().get().replyIndex()).isIn(0, 1);
            return llmResult("Because");
        });

        executor.execute(snapshot, "session-1", "Why?", conversation());
    }

    @Test
    void cancelsPendingRepliesWhenInterruptedDuringDelay() throws Exception {
        ConversationPlan plan = plan(
                ConversationGoal.STORY,
                ConversationEnergy.VERY_HIGH,
                ConversationArc.QUESTION_STORY,
                3,
                List.of(500L, 500L));
        ConversationPlanSnapshot snapshot = ConversationPlanSnapshot.planned(
                "session-1", plan, new ConversationPlanningTrace(List.of()));

        when(llmService.generate(eq("session-1"), eq("Tell me a story"))).thenReturn(llmResult("Part 1"));

        CompletableFuture<List<ConversationMessage>> execution = CompletableFuture.supplyAsync(
                () -> executor.execute(snapshot, "session-1", "Tell me a story", conversation()));
        Thread.sleep(50);
        executionRegistry.cancelPending("session-1", "New user message received");

        List<ConversationMessage> replies = execution.get(3, TimeUnit.SECONDS);

        assertThat(replies).hasSize(1);
        verify(llmService, times(1)).generate(eq("session-1"), eq("Tell me a story"));
    }

    @Test
    void lowEnergyPlanDeliversExactlyOneMessage() {
        ConversationPlan plan = plan(
                ConversationGoal.GOODBYE,
                ConversationEnergy.LOW,
                ConversationArc.GOODBYE,
                1,
                List.of());
        ConversationPlanSnapshot snapshot = ConversationPlanSnapshot.planned(
                "session-1", plan, new ConversationPlanningTrace(List.of()));

        when(llmService.generate(eq("session-1"), eq("Bye"))).thenReturn(llmResult("Farewell"));

        List<ConversationMessage> replies = executor.execute(snapshot, "session-1", "Bye", conversation());

        assertThat(replies).hasSize(1);
    }

    private static ConversationPlan plan(
            ConversationGoal goal,
            ConversationEnergy energy,
            ConversationArc arc,
            int expectedMessageCount,
            List<Long> delays) {
        return new ConversationPlan(
                goal,
                0.9,
                true,
                energy,
                arc,
                expectedMessageCount,
                delays,
                false,
                goal == ConversationGoal.STORY,
                false,
                false,
                goal == ConversationGoal.GOODBYE,
                "Warm",
                ConversationOutcome.RESOLVED,
                Instant.parse("2026-01-01T00:00:00Z"),
                false,
                false,
                null,
                null);
    }

    private static Conversation conversation() {
        return new Conversation(
                "conv-1",
                "session-1",
                new ConversationCharacter("c1", "Alpha", List.of("Alpha"), "Rabbitu", null),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z"),
                ConversationStatus.ACTIVE,
                List.of());
    }

    private static LLMGenerateResult llmResult(String reply) {
        return new LLMGenerateResult(
                new LLMProviderInfo(LLMProviderType.MOCK, "Mock", "Mock provider", true, "mock-bandar"),
                null,
                new ProviderResponse(reply, new ProviderTokenUsage(1, 2, 3), Map.of(), 10, "stop"));
    }
}
