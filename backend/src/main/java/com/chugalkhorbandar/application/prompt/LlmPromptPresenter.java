package com.chugalkhorbandar.application.prompt;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;

public final class LlmPromptPresenter {

    private LlmPromptPresenter() {}

    public static String format(PromptSection section) {
        String heading = heading(section);
        String body = body(section);
        if (heading == null || heading.isBlank()) {
            return body;
        }
        return heading + "\n\n" + body;
    }

    public static String heading(PromptSection section) {
        if (section.fragmentType() == KnowledgeFragmentType.IDENTITY) {
            return "Your Identity";
        }
        if (section.fragmentType() == KnowledgeFragmentType.SPEAKING_STYLE) {
            return "Your Speaking Style";
        }
        if (section.fragmentType() == KnowledgeFragmentType.STORYTELLING) {
            return "Your Storytelling Style";
        }
        if (section.fragmentType() == KnowledgeFragmentType.HUMOR) {
            return "Your Sense of Humor";
        }
        if (section.fragmentType() == KnowledgeFragmentType.SECRET_POLICY) {
            return "Your Secret Policy";
        }
        if (section.fragmentType() == KnowledgeFragmentType.WORKING_MEMORY) {
            return "Current Train of Thought";
        }
        return switch (section.sectionType()) {
            case CURRENT_USER -> "The Current Speaker";
            case RELATIONSHIP_TO_BANDAR -> "Your Relationship";
            case WORKING_MEMORY -> "Current Train of Thought";
            case PERSONALITY -> "Your Character";
            case WORLD_FACTS,
                    CURRENT_CHARACTER,
                    CURRENT_LOCATION,
                    RELATIONSHIPS,
                    RELEVANT_STORIES,
                    SESSION_SUMMARY,
                    PUBLIC_EVENTS,
                    LONG_TERM_MEMORY,
                    SECRET_MEMORY,
                    UNKNOWN -> "Relevant Knowledge";
            case CURRENT_CONVERSATION -> "Current Conversation";
            case CONVERSATION_STYLE -> "Conversation Style";
            case INSTRUCTIONS -> "Instructions";
            case USER_MESSAGE -> null;
        };
    }

    public static String body(PromptSection section) {
        if (section.fragmentType() == KnowledgeFragmentType.IDENTITY) {
            return identityBody(section.content());
        }
        if (section.sectionType() == PromptSectionType.RELATIONSHIP_TO_BANDAR) {
            return relationshipBody(section.content());
        }
        if (section.sectionType() == PromptSectionType.INSTRUCTIONS) {
            return instructionsBody(section.content());
        }
        if (section.sectionType() == PromptSectionType.CURRENT_USER) {
            return currentSpeakerBody(section.content());
        }
        return section.content() == null ? "" : section.content().trim();
    }

    private static String identityBody(String rawContent) {
        String knowledge = rawContent == null ? "" : rawContent.trim();
        return """
                You are Bandar.
                Speak about yourself using "I", "me" and "my".
                Never refer to Bandar as someone separate from yourself.
                Never introduce yourself as a generic assistant.
                Never suggest visiting Bandar or going to find Bandar.
                Never describe Bandar as another character.

                %s"""
                .formatted(knowledge)
                .trim();
    }

    private static String currentSpeakerBody(String rawContent) {
        String details = rawContent == null ? "" : rawContent.trim();
        if (details.startsWith("The character currently speaking with you is:")) {
            details = details.substring("The character currently speaking with you is:".length()).trim();
        }
        return """
                The one speaking with you now is:

                %s

                Treat every message in this conversation as spoken by this character unless explicitly told otherwise."""
                .formatted(details)
                .trim();
    }

    private static String relationshipBody(String rawContent) {
        String relationship = rawContent == null ? "" : rawContent.trim();
        return """
                %s

                Let this relationship naturally shape your tone when you reply.
                Do not recite these facts mechanically — express respect, warmth, fondness, or familiarity as the relationship warrants."""
                .formatted(relationship)
                .trim();
    }

    private static String instructionsBody(String rawContent) {
        String rules = rawContent == null ? "" : rawContent.trim();
        rules = rules.replace(
                "The Current User section identifies who is speaking.",
                "The Current Speaker section identifies who is speaking.");
        rules = rules.replace(
                "if the Current User section is present",
                "if The Current Speaker section is present");
        return rules
                + """

                Remember: you are Bandar. Always speak about yourself in the first person.
                Never send the speaker to find Bandar or describe Bandar as someone else.
                Speak from memory when you witnessed events. Never sound like a counsellor or generic assistant."""
                        .trim();
    }
}
