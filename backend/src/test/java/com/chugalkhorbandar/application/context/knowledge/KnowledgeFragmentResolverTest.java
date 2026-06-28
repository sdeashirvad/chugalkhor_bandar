package com.chugalkhorbandar.application.context.knowledge;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeFragmentResolverTest {

    private KnowledgeFragmentResolver resolver;
    private KnowledgeFragmentPlanner planner;
    private InMemoryWorldRepositoryProvider repositories;

    @BeforeEach
    void setUp() {
        InMemoryWorldStore store = new InMemoryWorldStore();
        repositories = new InMemoryWorldRepositoryProvider(store);
        store.promptProfiles()
                .put(
                        "prompt_bandar_personality",
                        new RuntimePromptProfile(
                                "prompt_bandar_personality",
                                "Bandar",
                                Map.of(
                                        "identity", "I am Bandar, the oldest living being.",
                                        "speakingStyle", "Speak warmly and simply.")));
        store.characters()
                .put(
                        "character_hippu_king",
                        new RuntimeCharacter(
                                "character_hippu_king",
                                "Hippu King",
                                Map.of(
                                        "summary", "Hippu King rules 176 jungles.",
                                        "titles", "King of 176 Jungles"),
                                "place_hippu_palace",
                                Map.of()));
        store.places()
                .put(
                        "place_hippu_palace",
                        new com.chugalkhorbandar.domain.world.runtime.RuntimePlace(
                                "place_hippu_palace",
                                "Hippu Palace",
                                Map.of("type", "Royal Residence", "locatedIn", "Home Jungle")));
        resolver = KnowledgeFragmentTestSupport.fragmentResolver(repositories);
        planner = KnowledgeFragmentTestSupport.fragmentPlanner(repositories);
    }

    @Test
    void resolvesFineGrainedFragmentsForLocationQuestion() {
        ContextPlannerRequest request = request("Where am I?");
        KnowledgeFragmentPlan fragmentPlan = planner.plan(request);

        List<KnowledgeFragment> fragments = resolver.resolve(fragmentPlan.requests(), request);

        assertThat(fragments).extracting(KnowledgeFragment::fragmentType)
                .contains(
                        KnowledgeFragmentType.IDENTITY,
                        KnowledgeFragmentType.CHARACTER_PROFILE,
                        KnowledgeFragmentType.CHARACTER_LOCATION);
        assertThat(fragments).extracting(KnowledgeFragment::fragmentType)
                .doesNotContain(KnowledgeFragmentType.WORLD_HISTORY, KnowledgeFragmentType.WORLD_ECONOMY);
        assertThat(fragments.stream().filter(fragment -> fragment.fragmentType() == KnowledgeFragmentType.IDENTITY)
                        .findFirst()
                        .orElseThrow()
                        .content())
                .contains("I am Bandar");
    }

    @Test
    void registersFragmentsInRegistry() {
        ContextPlannerRequest request = request("Where am I?");
        KnowledgeFragmentPlan fragmentPlan = planner.plan(request);
        KnowledgeFragmentRegistry registry = new KnowledgeFragmentRegistry();
        KnowledgeFragmentResolver resolvingRegistry =
                new KnowledgeFragmentResolver(KnowledgeFragmentTestSupport.knowledgeProviders(repositories), registry);

        resolvingRegistry.resolve(fragmentPlan.requests(), request);

        assertThat(registry.findByType(KnowledgeFragmentType.IDENTITY)).isNotEmpty();
        assertThat(registry.findByTag("location")).isNotEmpty();
    }

    private static ContextPlannerRequest request(String latestMessage) {
        CurrentCharacter character = new CurrentCharacter(
                "character_hippu_king", "Hippu King", List.of("King"), "Hippu", "Hippu Kingdom", "Hippu Palace");
        ChatSession session = new ChatSession(
                "session-1", character, Instant.now(), Instant.now(), SessionStatus.ACTIVE);
        return new ContextPlannerRequest(character, session, null, latestMessage, null);
    }
}
