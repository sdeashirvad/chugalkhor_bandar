package com.chugalkhorbandar.application.context.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlanner;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentTestSupport;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextResolverTest {

    private ContextPlanner planner;
    private ContextResolver resolver;

    @BeforeEach
    void setUp() {
        InMemoryWorldStore store = new InMemoryWorldStore();
        InMemoryWorldRepositoryProvider repositories = new InMemoryWorldRepositoryProvider(store);
        store.promptProfiles()
                .put(
                        "prompt_bandar_personality",
                        new RuntimePromptProfile("prompt_bandar_personality", "Bandar", Map.of("identity", "Monkey narrator")));
        store.characters()
                .put(
                        "character_alpha",
                        new RuntimeCharacter(
                                "character_alpha",
                                "Alpha",
                                Map.of("summary", "Curious leader", "personality", "Curious", "secrets", "Hidden"),
                                null,
                                Map.of()));

        planner = new ContextPlanner(KnowledgeFragmentTestSupport.fragmentPlanner(repositories));
        resolver = new ContextResolver(KnowledgeFragmentTestSupport.fragmentResolver(repositories));
    }

    @Test
    void resolvesFragmentsAndLegacySections() {
        ContextPlannerRequest request = request("Hello");
        ContextPlan plan = planner.plan(request);
        ResolvedContext resolved = resolver.resolve(plan, request);

        assertThat(resolved.fragments()).isNotEmpty();
        assertThat(resolved.fragments().stream().map(fragment -> fragment.fragmentType()))
                .contains(KnowledgeFragmentType.IDENTITY);
        assertThat(resolved.sections().stream().map(ResolvedContextSection::type)).contains(ContextSectionType.PERSONALITY);
        assertThat(resolved.fragments().stream()
                        .filter(fragment -> fragment.fragmentType() == KnowledgeFragmentType.IDENTITY)
                        .findFirst()
                        .orElseThrow()
                        .content())
                .contains("Monkey narrator");
    }

    @Test
    void excludesSecretsFromCharacterFragments() {
        ContextPlannerRequest request = request("Tell me about the king");
        ResolvedContext resolved = resolver.resolve(planner.plan(request), request);

        assertThat(resolved.fragments().stream().map(fragment -> fragment.content()).anyMatch(content -> content.contains("Curious")))
                .isTrue();
        assertThat(resolved.fragments().stream().map(fragment -> fragment.content()).anyMatch(content -> content.contains("Hidden")))
                .isFalse();
    }

    @Test
    void preservesSectionOrdering() {
        ResolvedContext resolved = resolver.resolve(planner.plan(request("Where is the king's story?")), request("Where is the king's story?"));
        List<Integer> priorities = resolved.sections().stream().map(ResolvedContextSection::priority).toList();
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
        RuntimeWorldContext world = new RuntimeWorldContext("READY", "1.0", 1, 0, List.of());
        return new ContextPlannerRequest(character, session, conversation, latestMessage, world);
    }
}
