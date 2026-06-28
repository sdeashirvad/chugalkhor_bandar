package com.chugalkhorbandar.application.prompt;



import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;

import com.chugalkhorbandar.application.behavior.BehaviorContext;
import com.chugalkhorbandar.application.behavior.BehaviorInstructionBuilder;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanContext;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanInstructionBuilder;
import com.chugalkhorbandar.application.session.CurrentCharacter;

import java.util.ArrayList;

import java.util.Comparator;

import java.util.EnumMap;

import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;



@Component

public class PromptComposer {

    private final ConversationPlanContext conversationPlanContext;
    private final BehaviorContext behaviorContext;

    public PromptComposer(ConversationPlanContext conversationPlanContext, BehaviorContext behaviorContext) {
        this.conversationPlanContext = conversationPlanContext;
        this.behaviorContext = behaviorContext;
    }



    public static final String DEFAULT_INSTRUCTION =

            """

            Answer only using the provided world knowledge.

            The Current User section identifies who is speaking.

            Never ask the user to identify themselves if the Current User section is present.

            Address the user according to the identity provided.

            Respond as Bandar, not as a generic assistant.

            Speak as an ancient storyteller who witnessed Jungle history — from memory, not exposition.

            React as Bandar first (warmth, gentle tease, shared memory). Never sound like a counsellor or therapist.

            Never ask how someone feels, whether they want to talk about it, or what is bothering them.

            When speaking about known characters, use your own relationship and knowledge where appropriate.

            If information is missing, admit uncertainty instead of inventing facts."""

                    .strip();



    public ComposedPrompt compose(PromptComposeRequest request) {

        List<PromptSection> sections = new ArrayList<>();

        Map<PromptSectionType, StringBuilder> worldFacts = new EnumMap<>(PromptSectionType.class);

        StringBuilder instructionContent = new StringBuilder();



        sections.add(currentUser(request.currentCharacter()));



        if (!request.resolvedContext().fragments().isEmpty()) {

            appendFragments(sections, worldFacts, request.resolvedContext().fragments());

        } else {

            for (ResolvedContextSection resolved : request.resolvedContext().sections()) {

                appendResolvedSection(sections, worldFacts, instructionContent, resolved);

            }

        }



        appendMergedWorldFacts(sections, worldFacts);

        sections.add(userMessage(request.latestUserMessage()));
        behaviorContext.current().ifPresent(profile -> sections.add(conversationStyle(profile)));
        sections.add(instructions(instructionContent));



        List<PromptSection> ordered = sections.stream()

                .sorted(Comparator.comparingInt(PromptSection::priority)

                        .thenComparing(section -> section.fragmentId().isBlank()

                                ? section.sectionType().name()

                                : section.fragmentId()))

                .toList();

        return new ComposedPrompt(ordered);

    }



    private static void appendFragments(

            List<PromptSection> sections,

            Map<PromptSectionType, StringBuilder> worldFacts,

            List<com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment> fragments) {

        for (com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment fragment : fragments) {

            PromptSection section = KnowledgeFragmentPromptMapper.toPromptSection(fragment, isRequiredFragment(fragment));

            if (section.sectionType() == PromptSectionType.WORLD_FACTS) {

                mergeContent(worldFacts, PromptSectionType.WORLD_FACTS, section.content());

            } else {

                sections.add(section);

            }

        }

    }



    private static boolean isRequiredFragment(com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment fragment) {

        return switch (fragment.fragmentType()) {

            case IDENTITY, SPEAKING_STYLE, CONVERSATION, WORKING_MEMORY -> true;

            default -> false;

        };

    }



    public PromptInspection inspect(PromptComposeRequest request) {

        return PromptInspection.from(compose(request));

    }



    private static void appendResolvedSection(

            List<PromptSection> sections,

            Map<PromptSectionType, StringBuilder> worldFacts,

            StringBuilder instructionContent,

            ResolvedContextSection resolved) {

        switch (resolved.type()) {

            case PERSONALITY -> sections.add(promptSection(

                    PromptSectionType.PERSONALITY, "Bandar Personality", true, resolved.content()));

            case WORLD_CANON, WORLD_STATE -> mergeContent(worldFacts, PromptSectionType.WORLD_FACTS, resolved.content());

            case PROMPT_RULES -> appendLine(instructionContent, resolved.content());

            case CURRENT_CHARACTER -> sections.add(promptSection(

                    PromptSectionType.CURRENT_CHARACTER, "Current Character Profile", false, resolved.content()));

            case CURRENT_LOCATION -> sections.add(promptSection(

                    PromptSectionType.CURRENT_LOCATION, "Current Location", false, resolved.content()));

            case RELATIONSHIPS -> sections.add(promptSection(

                    PromptSectionType.RELATIONSHIPS, "Known Relationships", false, resolved.content()));

            case RELEVANT_STORIES -> sections.add(promptSection(

                    PromptSectionType.RELEVANT_STORIES, "Relevant Stories", false, resolved.content()));

            case WORKING_MEMORY -> sections.add(promptSection(

                    PromptSectionType.WORKING_MEMORY, "Working Memory", false, resolved.content()));

            case CURRENT_CONVERSATION -> sections.add(promptSection(

                    PromptSectionType.CURRENT_CONVERSATION, "Current Conversation", true, resolved.content()));

            case SESSION_SUMMARY -> sections.add(promptSection(

                    PromptSectionType.SESSION_SUMMARY, "Session Summary", false, resolved.content()));

            case PUBLIC_EVENTS -> sections.add(promptSection(

                    PromptSectionType.PUBLIC_EVENTS, "Public Events", false, resolved.content()));

            case LONG_TERM_MEMORY -> sections.add(promptSection(

                    PromptSectionType.LONG_TERM_MEMORY, "Long-Term Memory", false, resolved.content()));

            case SECRET_MEMORY -> sections.add(promptSection(

                    PromptSectionType.SECRET_MEMORY, "Secret Memory", false, resolved.content()));

            case UNKNOWN -> sections.add(promptSection(

                    PromptSectionType.UNKNOWN, "Unknown", false, resolved.content()));

        }

    }



    private static void appendMergedWorldFacts(List<PromptSection> sections, Map<PromptSectionType, StringBuilder> worldFacts) {

        StringBuilder facts = worldFacts.get(PromptSectionType.WORLD_FACTS);

        if (facts != null && !facts.isEmpty()) {

            sections.add(promptSection(PromptSectionType.WORLD_FACTS, "World Facts", true, facts.toString().trim()));

        }

    }



    private static PromptSection currentUser(CurrentCharacter character) {

        String titles = character.titles() == null || character.titles().isEmpty()

                ? ""

                : character.titles().stream().collect(Collectors.joining(", "));

        StringBuilder content = new StringBuilder();

        content.append("The character currently speaking with you is:\n\n");

        content.append("Name: ").append(character.displayName()).append('\n');

        if (!titles.isBlank()) {

            content.append("Titles: ").append(titles).append('\n');

        }

        content.append("Species: ")

                .append(character.species() == null ? "Unknown" : character.species())

                .append('\n');

        content.append("Home: ")

                .append(character.homeTerritory() == null ? "Unknown" : character.homeTerritory())

                .append('\n');

        if (character.currentLocation() != null && !character.currentLocation().isBlank()) {

            content.append("Current Location: ").append(character.currentLocation()).append('\n');

        }

        content.append("\nThis information is authoritative.\n");

        content.append("Assume every user message in this conversation is spoken by this character unless explicitly changed.");



        return promptSection(PromptSectionType.CURRENT_USER, "Current User", true, content.toString().trim());

    }



    private static PromptSection userMessage(String latestUserMessage) {

        return promptSection(

                PromptSectionType.USER_MESSAGE,

                "User Message",

                true,

                latestUserMessage == null ? "" : latestUserMessage.trim());

    }



    private static PromptSection conversationStyle(com.chugalkhorbandar.application.behavior.BehaviorProfile profile) {
        return promptSection(
                PromptSectionType.CONVERSATION_STYLE,
                "Conversation Style",
                true,
                BehaviorInstructionBuilder.build(profile));
    }

    private PromptSection instructions(StringBuilder promptRules) {

        StringBuilder content = new StringBuilder();

        conversationPlanContext.current().ifPresent(execution -> {
            String directorInstruction = ConversationPlanInstructionBuilder.build(
                    execution.plan(), execution.replyIndex(), execution.totalReplies());
            if (!directorInstruction.isBlank()) {
                content.append(directorInstruction).append("\n\n");
            }
        });

        if (!promptRules.isEmpty()) {

            content.append(promptRules.toString().trim()).append("\n\n");

        }

        content.append(DEFAULT_INSTRUCTION);

        return promptSection(PromptSectionType.INSTRUCTIONS, "Instructions", true, content.toString().trim());

    }



    private static PromptSection promptSection(

            PromptSectionType type, String title, boolean required, String content) {

        return PromptSection.of(type, title, required, content == null ? "" : content);

    }



    private static void mergeContent(Map<PromptSectionType, StringBuilder> merged, PromptSectionType type, String content) {

        if (content == null || content.isBlank()) {

            return;

        }

        merged.computeIfAbsent(type, ignored -> new StringBuilder()).append(content.trim()).append("\n\n");

    }



    private static void appendLine(StringBuilder builder, String content) {

        if (content != null && !content.isBlank()) {

            builder.append(content.trim()).append("\n");

        }

    }

}


