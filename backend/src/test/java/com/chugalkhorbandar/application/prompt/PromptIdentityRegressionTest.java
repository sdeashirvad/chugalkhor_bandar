package com.chugalkhorbandar.application.prompt;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlan;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentResolver;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentSelector;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentTestSupport;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileCatalog;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileSelector;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileType;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.config.PromptProfileProperties;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PromptIdentityRegressionTest {

    private KnowledgeFragmentSelector fragmentSelector;
    private ContextProfileSelector profileSelector;
    private KnowledgeFragmentResolver fragmentResolver;
    private PromptComposer promptComposer;
    private InMemoryWorldRepositoryProvider repositories;

    @BeforeEach
    void setUp() {
        InMemoryWorldStore store = new InMemoryWorldStore();
        repositories = new InMemoryWorldRepositoryProvider(store);
        fragmentSelector = new KnowledgeFragmentSelector();
        profileSelector = new ContextProfileSelector(new ContextProfileCatalog(new PromptProfileProperties()));
        fragmentResolver = KnowledgeFragmentTestSupport.fragmentResolver(repositories);
        promptComposer = new PromptComposer(
                new com.chugalkhorbandar.application.conversation.director.ConversationPlanContext(),
                new com.chugalkhorbandar.application.behavior.BehaviorContext());
        seedWorld(store);
    }

    @Test
    void whoAmISelectsIdentityQueryProfile() {
        var selection = profileSelector.select(contextRequest("Who am I?"), KnowledgeFragmentTestSupport.emptyContextPlan(), emptyResolved());

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.IDENTITY_QUERY);
    }

    @Test
    void whoAmISelectsIdentityFragments() {
        var selections = fragmentSelector.select("Who am I?");

        assertThat(selections.keySet())
                .contains(
                        KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR,
                        KnowledgeFragmentType.CHARACTER_PROFILE,
                        KnowledgeFragmentType.CHARACTER_TITLES);
        assertThat(selections.keySet())
                .doesNotContain(KnowledgeFragmentType.WORLD_ECONOMY, KnowledgeFragmentType.STORY_SUMMARY);
    }

    @Test
    void currentUserSectionAlwaysPresentForAuthenticatedSession() {
        ComposedPrompt composed = promptComposer.compose(composeRequest("Hello"));

        assertThat(composed.sections()).anyMatch(section -> section.sectionType() == PromptSectionType.CURRENT_USER);
    }

    @Test
    void instructionsPreventAskingUserToIdentifyThemselves() {
        ComposedPrompt composed = promptComposer.compose(composeRequest("Hello"));

        PromptSection instructions = composed.sections().stream()
                .filter(section -> section.sectionType() == PromptSectionType.INSTRUCTIONS)
                .findFirst()
                .orElseThrow();

        assertThat(instructions.content())
                .contains("Never ask the user to identify themselves if the Current User section is present");
    }

    @Test
    void instructionsDiscourageCounsellorToneAndEncourageWitnessVoice() {
        ComposedPrompt composed = promptComposer.compose(composeRequest("Hello"));

        PromptSection instructions = composed.sections().stream()
                .filter(section -> section.sectionType() == PromptSectionType.INSTRUCTIONS)
                .findFirst()
                .orElseThrow();

        assertThat(instructions.content())
                .contains("Never sound like a counsellor or therapist")
                .contains("Speak as an ancient storyteller who witnessed Jungle history");
    }

    @Test
    void relationshipFragmentIncludedForIdentityQuery() {
        ContextPlannerRequest request = contextRequest("Who am I?");
        KnowledgeFragmentPlan fragmentPlan = KnowledgeFragmentTestSupport.fragmentPlanner(repositories).plan(request);
        List<KnowledgeFragment> fragments = fragmentResolver.resolve(fragmentPlan.requests(), request);

        assertThat(fragments).anyMatch(fragment -> fragment.fragmentType() == KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR);
        assertThat(fragments.stream()
                        .filter(fragment -> fragment.fragmentType() == KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR)
                        .findFirst()
                        .orElseThrow()
                        .content())
                .contains("Hippu King");
    }

    @Test
    void composedPromptContainsNoInternalIds() {
        ComposedPrompt composed = promptComposer.compose(composeRequest("Who am I?"));

        assertThat(composed.sections().stream().map(PromptSection::content).toList())
                .allSatisfy(content -> {
                    assertThat(content).doesNotContain("character_hippu_king");
                    assertThat(content).doesNotContain("territory_hippu_kingdom");
                    assertThat(content).doesNotContain("place_hippu_palace");
                });
    }

    @Test
    void fragmentTitlesAreHumanReadable() {
        ContextPlannerRequest request = contextRequest("Hello Bandar");
        KnowledgeFragmentPlan fragmentPlan = KnowledgeFragmentTestSupport.fragmentPlanner(repositories).plan(request);
        List<KnowledgeFragment> fragments = fragmentResolver.resolve(fragmentPlan.requests(), request);

        assertThat(fragments.stream().map(KnowledgeFragment::title))
                .anyMatch(title -> title.startsWith("Bandar "));
    }

    private PromptComposeRequest composeRequest(String latestMessage) {
        ContextPlannerRequest request = contextRequest(latestMessage);
        KnowledgeFragmentPlan fragmentPlan = KnowledgeFragmentTestSupport.fragmentPlanner(repositories).plan(request);
        List<KnowledgeFragment> fragments = fragmentResolver.resolve(fragmentPlan.requests(), request);
        return new PromptComposeRequest(new ResolvedContext(List.of(), fragments, 100), latestMessage, request.currentCharacter(), request.session(), null);
    }

    private static ResolvedContext emptyResolved() {
        return new ResolvedContext(List.of(), List.of(), 0);
    }

    private static ContextPlannerRequest contextRequest(String latestMessage) {
        CurrentCharacter character = new CurrentCharacter(
                "character_hippu_king", "Hippu King", List.of("King"), "Hippu", "Hippu Kingdom", "Hippu Palace");
        ChatSession session = new ChatSession(
                "session-1", character, Instant.now(), Instant.now(), SessionStatus.ACTIVE);
        return new ContextPlannerRequest(character, session, null, latestMessage, null);
    }

    private static void seedWorld(InMemoryWorldStore store) {
        store.promptProfiles()
                .put(
                        "prompt_bandar_personality",
                        new RuntimePromptProfile(
                                "prompt_bandar_personality",
                                "Bandar",
                                Map.of(
                                        "identity", "I am Bandar, the oldest living being.",
                                        "speakingStyle", "Speak warmly and simply.",
                                        "attitudeTowardCharacters",
                                        """
                                        ### Hippu King
                                        Bandar respects Hippu King for balancing strength with kindness.
                                        """)));
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
        store.territories()
                .put(
                        "territory_hippu_kingdom",
                        new RuntimeTerritory("territory_hippu_kingdom", "Hippu Kingdom", Map.of(), null));
    }
}
