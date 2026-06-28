package com.chugalkhorbandar.application.conversation.director;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.context.ContextRequestFactory;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryProperties;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.session.SessionService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationDirectorServiceExecutionTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private ContextRequestFactory contextRequestFactory;

    @Mock
    private WorkingMemoryService workingMemoryService;

    @Mock
    private ConversationDirector director;

    private InMemoryConversationPlanStore store;
    private ConversationExecutionRegistry executionRegistry;
    private ConversationDirectorService service;

    @BeforeEach
    void setUp() {
        store = new InMemoryConversationPlanStore();
        executionRegistry = new ConversationExecutionRegistry();
        service = new ConversationDirectorService(
                sessionService,
                contextRequestFactory,
                workingMemoryService,
                new WorkingMemoryProperties(),
                director,
                store,
                executionRegistry);
    }

    @Test
    void markExecutionFinishedRecordsCompletion() {
        ConversationPlan plan = new ConversationPlan(
                ConversationGoal.STORY,
                0.94,
                true,
                ConversationEnergy.HIGH,
                ConversationArc.QUESTION_STORY,
                2,
                List.of(100L),
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
        store.save(ConversationPlanSnapshot.planned("session-1", plan, new ConversationPlanningTrace(List.of())));

        Instant started = Instant.parse("2026-01-01T00:00:01Z");
        Instant completed = Instant.parse("2026-01-01T00:00:05Z");
        service.markExecutionFinished(
                "session-1",
                plan.createdAt(),
                new ConversationExecutionResult(
                        2,
                        0,
                        "",
                        List.of(new ConversationExecutionTimelineEntry(0, "MESSAGE_DELIVERED", completed, 0)),
                        List.of("msg-1", "msg-2"),
                        List.of(),
                        started,
                        completed,
                        false,
                        true));

        ConversationPlanSnapshot snapshot = store.findBySessionId("session-1").orElseThrow();
        assertThat(snapshot.executed()).isTrue();
        assertThat(snapshot.executedMessageCount()).isEqualTo(2);
        assertThat(snapshot.plan().completedAt()).isEqualTo(completed);
        assertThat(snapshot.deliveredMessageIds()).containsExactly("msg-1", "msg-2");
    }

    @Test
    void markExecutionFinishedIgnoresStalePlan() {
        ConversationPlan oldPlan = new ConversationPlan(
                ConversationGoal.SMALL_TALK,
                0.65,
                true,
                ConversationEnergy.LOW,
                ConversationArc.SMALL_TALK,
                1,
                List.of(),
                false,
                false,
                false,
                false,
                false,
                "Thoughtful",
                ConversationOutcome.UNRESOLVED,
                Instant.parse("2026-01-01T00:00:00Z"),
                false,
                false,
                null,
                null);
        ConversationPlan newPlan = new ConversationPlan(
                ConversationGoal.GREETING,
                0.92,
                true,
                ConversationEnergy.LOW,
                ConversationArc.GREETING_REPLY,
                1,
                List.of(),
                false,
                false,
                false,
                false,
                false,
                "Warm",
                ConversationOutcome.FOLLOW_UP_REQUIRED,
                Instant.parse("2026-01-01T00:00:10Z"),
                false,
                false,
                null,
                null);
        store.save(ConversationPlanSnapshot.planned("session-1", newPlan, new ConversationPlanningTrace(List.of())));

        service.markExecutionFinished(
                "session-1",
                oldPlan.createdAt(),
                new ConversationExecutionResult(
                        1,
                        2,
                        "New user message received",
                        List.of(),
                        List.of("stale"),
                        List.of(1, 2),
                        Instant.now(),
                        Instant.now(),
                        true,
                        false));

        ConversationPlanSnapshot snapshot = store.findBySessionId("session-1").orElseThrow();
        assertThat(snapshot.plan().createdAt()).isEqualTo(newPlan.createdAt());
        assertThat(snapshot.executed()).isFalse();
    }

    @Test
    void cancelPendingExecutionCancelsActiveHandle() {
        ConversationExecutionHandle handle = executionRegistry.begin("session-1");
        service.cancelPendingExecution("session-1", "New user message received");
        assertThat(handle.isCancelled()).isTrue();
        assertThat(handle.interruptionReason()).isEqualTo("New user message received");
    }
}
