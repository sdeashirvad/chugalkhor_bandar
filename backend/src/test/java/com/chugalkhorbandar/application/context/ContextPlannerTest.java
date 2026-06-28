package com.chugalkhorbandar.application.context;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentTestSupport;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextPlannerTest {

    private ContextPlanner planner;

    @BeforeEach
    void setUp() {
        InMemoryWorldStore store = new InMemoryWorldStore();
        InMemoryWorldRepositoryProvider repositories = new InMemoryWorldRepositoryProvider(store);
        store.promptProfiles()
                .put("prompt_bandar_personality", new RuntimePromptProfile("prompt_bandar_personality", "Bandar", Map.of()));
        store.stories()
                .put(
                        "story_king_tale",
                        new RuntimeStory("story_king_tale", "King's Tale", Map.of("summary", "A tale about the king."), Map.of()));
        planner = new ContextPlanner(KnowledgeFragmentTestSupport.fragmentPlanner(repositories));
    }

    @Test
    void alwaysIncludesCoreFragments() {
        ContextPlan plan = planner.plan(request("Hello Bandar"));

        assertThat(plan.fragmentPlan().requests().stream().map(requestItem -> requestItem.fragmentType()))
                .contains(
                        KnowledgeFragmentType.IDENTITY,
                        KnowledgeFragmentType.SPEAKING_STYLE,
                        KnowledgeFragmentType.CONVERSATION);
    }

    @Test
    void selectsLocationFragmentsWhenUserAsksWhere() {
        ContextPlan plan = planner.plan(request("Where am I?"));

        assertThat(plan.fragmentPlan().requests().stream().map(requestItem -> requestItem.fragmentType()))
                .contains(KnowledgeFragmentType.CHARACTER_LOCATION, KnowledgeFragmentType.CHARACTER_PROFILE);
        assertThat(plan.fragmentPlan().trace().entries())
                .anyMatch(entry -> entry.fragmentType() == KnowledgeFragmentType.CHARACTER_LOCATION
                        && entry.reason().contains("where"));
    }

    @Test
    void selectsStoryFragmentsFromKeywords() {
        ContextPlan plan = planner.plan(request("Tell me a story about the king"));

        assertThat(plan.fragmentPlan().requests().stream().map(requestItem -> requestItem.fragmentType()))
                .contains(
                        KnowledgeFragmentType.STORY_SUMMARY,
                        KnowledgeFragmentType.STORYTELLING,
                        KnowledgeFragmentType.CHARACTER_RELATIONSHIPS);
    }

    @Test
    void ordersSectionsByPriority() {
        ContextPlan plan = planner.plan(request("Where is the king's story?"));

        List<Integer> priorities = plan.sections().stream().map(ContextSection::priority).toList();
        assertThat(priorities).isSorted();
    }

    private static ContextPlannerRequest request(String latestMessage) {
        CurrentCharacter character = new CurrentCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null, null);
        ChatSession session = new ChatSession(
                "session-1",
                character,
                Instant.parse("2026-06-27T12:00:00Z"),
                Instant.parse("2026-06-27T12:00:00Z"),
                SessionStatus.ACTIVE);
        Conversation conversation = new Conversation(
                "conversation-1",
                "session-1",
                new ConversationCharacter("character_alpha", "Alpha", List.of("Alpha"), "Rabbitu", null),
                Instant.parse("2026-06-27T12:00:00Z"),
                Instant.parse("2026-06-27T12:00:00Z"),
                ConversationStatus.ACTIVE,
                List.of());
        RuntimeWorldContext world = new RuntimeWorldContext("READY", "1.0", 13, 3, List.of());
        return new ContextPlannerRequest(character, session, conversation, latestMessage, world);
    }
}
