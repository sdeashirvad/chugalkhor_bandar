package com.chugalkhorbandar.application.prompt;



import static org.assertj.core.api.Assertions.assertThat;



import com.chugalkhorbandar.application.context.ContextReference;

import com.chugalkhorbandar.application.context.ContextSection;

import com.chugalkhorbandar.application.context.ContextSectionType;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;

import com.chugalkhorbandar.application.context.resolver.ResolvedContext;

import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;

import com.chugalkhorbandar.application.session.ChatSession;

import com.chugalkhorbandar.application.session.CurrentCharacter;

import com.chugalkhorbandar.application.session.SessionStatus;

import com.chugalkhorbandar.domain.conversation.Conversation;

import com.chugalkhorbandar.domain.conversation.ConversationCharacter;

import com.chugalkhorbandar.domain.conversation.ConversationStatus;

import java.time.Instant;

import java.util.List;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;



class PromptComposerTest {



    private PromptComposer composer;



    @BeforeEach

    void setUp() {

        composer = new PromptComposer(
                new com.chugalkhorbandar.application.conversation.director.ConversationPlanContext(),
                new com.chugalkhorbandar.application.behavior.BehaviorContext());

    }



    @Test

    void ordersSectionsAccordingToCompositionRules() {

        ComposedPrompt composed = composer.compose(sampleRequest("Where am I?", sampleResolvedContext()));



        List<PromptSectionType> order = composed.sections().stream()

                .map(PromptSection::sectionType)

                .toList();



        assertThat(order.indexOf(PromptSectionType.CURRENT_USER)).isLessThan(order.indexOf(PromptSectionType.PERSONALITY));

        assertThat(order.indexOf(PromptSectionType.PERSONALITY)).isLessThan(order.indexOf(PromptSectionType.WORLD_FACTS));

        assertThat(order.indexOf(PromptSectionType.WORLD_FACTS)).isLessThan(order.indexOf(PromptSectionType.CURRENT_CHARACTER));

        assertThat(order.indexOf(PromptSectionType.CURRENT_CHARACTER)).isLessThan(order.indexOf(PromptSectionType.CURRENT_CONVERSATION));

        assertThat(order.indexOf(PromptSectionType.CURRENT_CONVERSATION)).isLessThan(order.indexOf(PromptSectionType.USER_MESSAGE));

        assertThat(order.get(order.size() - 1)).isEqualTo(PromptSectionType.INSTRUCTIONS);

    }



    @Test

    void separatesRequiredAndOptionalSections() {

        ComposedPrompt composed = composer.compose(sampleRequest("Hello", sampleResolvedContext()));



        assertThat(composed.requiredSections()).extracting(PromptSection::sectionType)

                .contains(

                        PromptSectionType.CURRENT_USER,

                        PromptSectionType.PERSONALITY,

                        PromptSectionType.WORLD_FACTS,

                        PromptSectionType.CURRENT_CONVERSATION,

                        PromptSectionType.USER_MESSAGE,

                        PromptSectionType.INSTRUCTIONS);

        assertThat(composed.optionalSections()).extracting(PromptSection::sectionType)

                .contains(PromptSectionType.CURRENT_CHARACTER, PromptSectionType.CURRENT_LOCATION);

    }



    @Test

    void placesUserMessageBeforeInstructions() {

        ComposedPrompt composed = composer.compose(sampleRequest("Where am I in the Jungle?", sampleResolvedContext()));



        PromptSection userMessage = composed.sections().stream()

                .filter(section -> section.sectionType() == PromptSectionType.USER_MESSAGE)

                .findFirst()

                .orElseThrow();

        PromptSection instructions = composed.sections().stream()

                .filter(section -> section.sectionType() == PromptSectionType.INSTRUCTIONS)

                .findFirst()

                .orElseThrow();



        assertThat(userMessage.content()).isEqualTo("Where am I in the Jungle?");

        assertThat(instructions.content()).contains(PromptComposer.DEFAULT_INSTRUCTION);

        assertThat(userMessage.priority()).isLessThan(instructions.priority());

    }



    @Test

    void mergesWorldCanonAndWorldStateIntoWorldFacts() {

        ComposedPrompt composed = composer.compose(sampleRequest("Hello", sampleResolvedContext()));



        PromptSection worldFacts = composed.sections().stream()

                .filter(section -> section.sectionType() == PromptSectionType.WORLD_FACTS)

                .findFirst()

                .orElseThrow();



        assertThat(worldFacts.content()).contains("The Jungle exists.");

        assertThat(worldFacts.content()).contains("World is ready.");

        assertThat(worldFacts.required()).isTrue();

    }



    @Test

    void includesPromptRulesInInstructions() {

        ComposedPrompt composed = composer.compose(sampleRequest("Hello", sampleResolvedContext()));



        PromptSection instructions = composed.sections().stream()

                .filter(section -> section.sectionType() == PromptSectionType.INSTRUCTIONS)

                .findFirst()

                .orElseThrow();



        assertThat(instructions.content()).contains("Stay in character.");

        assertThat(instructions.content()).contains(PromptComposer.DEFAULT_INSTRUCTION);

        assertThat(instructions.content()).contains("Never ask the user to identify themselves");

    }



    @Test

    void calculatesTotalEstimatedTokens() {

        ComposedPrompt composed = composer.compose(sampleRequest("Hello", sampleResolvedContext()));



        assertThat(composed.totalEstimatedTokens())

                .isEqualTo(composed.sections().stream().mapToInt(PromptSection::estimatedTokens).sum());

    }



    @Test

    void promptInspectionSummarizesComposition() {

        PromptInspection inspection = composer.inspect(sampleRequest("Hello", sampleResolvedContext()));



        assertThat(inspection.sections()).hasSameSizeAs(composer.compose(sampleRequest("Hello", sampleResolvedContext())).sections());

        assertThat(inspection.totalEstimatedTokens()).isGreaterThan(0);

        assertThat(inspection.requiredSectionCount()).isGreaterThan(0);

        assertThat(inspection.optionalSectionCount()).isGreaterThan(0);

        assertThat(inspection.sections().getFirst().sectionType()).isEqualTo("CURRENT_USER");

    }



    @Test

    void currentUserUsesSessionCharacterWithoutInternalIds() {

        ComposedPrompt composed = composer.compose(sampleRequest("Hello", sampleResolvedContext()));



        PromptSection currentUser = composed.sections().stream()

                .filter(section -> section.sectionType() == PromptSectionType.CURRENT_USER)

                .findFirst()

                .orElseThrow();



        assertThat(currentUser.content()).contains("The character currently speaking with you is:");

        assertThat(currentUser.content()).contains("Hippu King");

        assertThat(currentUser.content()).contains("Hippu");

        assertThat(currentUser.content()).contains("Home: Hippu Kingdom");

        assertThat(currentUser.content()).contains("Current Location: Hippu Palace");

        assertThat(currentUser.content()).doesNotContain("character_hippu_king");

        assertThat(currentUser.content()).doesNotContain("Character ID");

    }



    @Test

    void composesPromptFromKnowledgeFragments() {

        ResolvedContext fragmentContext = new ResolvedContext(

                List.of(),

                List.of(

                        fragment(KnowledgeFragmentType.IDENTITY, "Bandar Identity", "I am Bandar."),

                        fragment(KnowledgeFragmentType.SPEAKING_STYLE, "Bandar Speaking Style", "Speak warmly."),

                        fragment(

                                KnowledgeFragmentType.CHARACTER_LOCATION,

                                "Current Location",

                                "Hippu King currently lives in Hippu Palace.")),

                20);



        ComposedPrompt composed = composer.compose(sampleRequest("Where am I?", fragmentContext));



        assertThat(composed.sections().stream().filter(PromptSection::isFragment).map(PromptSection::fragmentType))

                .containsExactlyInAnyOrder(

                        KnowledgeFragmentType.IDENTITY,

                        KnowledgeFragmentType.SPEAKING_STYLE,

                        KnowledgeFragmentType.CHARACTER_LOCATION);

        assertThat(composed.sections().stream()

                        .filter(section -> section.fragmentType() == KnowledgeFragmentType.CHARACTER_LOCATION)

                        .findFirst()

                        .orElseThrow()

                        .content())

                .contains("Hippu Palace");

        assertThat(composed.sections().stream()

                        .filter(section -> section.fragmentType() == KnowledgeFragmentType.IDENTITY)

                        .findFirst()

                        .orElseThrow()

                        .title())

                .isEqualTo("Bandar Identity");

    }



    private static PromptComposeRequest sampleRequest(String latestMessage, ResolvedContext resolvedContext) {

        CurrentCharacter character = new CurrentCharacter(

                "character_hippu_king", "Hippu King", List.of("King"), "Hippu", "Hippu Kingdom", "Hippu Palace");

        ChatSession session = new ChatSession(

                "session-1",

                character,

                Instant.parse("2026-06-27T12:00:00Z"),

                Instant.parse("2026-06-27T12:00:00Z"),

                SessionStatus.ACTIVE);

        Conversation conversation = new Conversation(

                "conversation-1",

                "session-1",

                new ConversationCharacter("character_hippu_king", "Hippu King", List.of("King"), "Hippu", "Hippu Kingdom"),

                Instant.parse("2026-06-27T12:00:00Z"),

                Instant.parse("2026-06-27T12:00:00Z"),

                ConversationStatus.ACTIVE,

                List.of());

        return new PromptComposeRequest(resolvedContext, latestMessage, character, session, conversation);

    }



    private static KnowledgeFragment fragment(KnowledgeFragmentType type, String title, String content) {

        return KnowledgeFragment.of(type, title, content, "test-source", type.name().toLowerCase(), Set.of(), 1.0);

    }



    private static ResolvedContext sampleResolvedContext() {

        return new ResolvedContext(

                List.of(

                        resolved(ContextSectionType.PERSONALITY, 10, "Bandar\nidentity: Monkey narrator"),

                        resolved(ContextSectionType.PROMPT_RULES, 20, "Stay in character."),

                        resolved(ContextSectionType.WORLD_CANON, 30, "The Jungle exists."),

                        resolved(ContextSectionType.WORLD_STATE, 40, "World is ready."),

                        resolved(ContextSectionType.CURRENT_CHARACTER, 50, "Hippu King\npersonality: Regal"),

                        resolved(ContextSectionType.CURRENT_LOCATION, 60, "Hippu Palace"),

                        resolved(ContextSectionType.CURRENT_CONVERSATION, 80, "User: Hello\nBandar: Welcome.")),

                List.of(),

                100);

    }



    private static ResolvedContextSection resolved(ContextSectionType type, int priority, String content) {

        return ResolvedContextSection.from(

                new ContextSection(

                        type,

                        priority,

                        "test",

                        new ContextReference("test", "entity", "entity-1", "attribute", priority),

                        ContextSection.estimateTokensFromContent(content)),

                content);

    }

}


