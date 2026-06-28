package com.chugalkhorbandar.application.prompt;

public final class PromptSectionPriorities {

    private PromptSectionPriorities() {}

    public static int priority(PromptSectionType type) {
        return switch (type) {
            case CURRENT_USER -> 5;
            case RELATIONSHIP_TO_BANDAR -> 10;
            case WORKING_MEMORY -> 12;
            case PERSONALITY -> 18;
            case WORLD_FACTS -> 40;
            case CURRENT_CHARACTER -> 45;
            case CURRENT_LOCATION -> 50;
            case RELATIONSHIPS -> 55;
            case RELEVANT_STORIES -> 60;
            case SESSION_SUMMARY -> 65;
            case PUBLIC_EVENTS -> 70;
            case LONG_TERM_MEMORY -> 75;
            case SECRET_MEMORY -> 80;
            case CURRENT_CONVERSATION -> 85;
            case USER_MESSAGE -> 900;
            case CONVERSATION_STYLE -> 925;
            case INSTRUCTIONS -> 950;
            case UNKNOWN -> 999;
        };
    }
}
