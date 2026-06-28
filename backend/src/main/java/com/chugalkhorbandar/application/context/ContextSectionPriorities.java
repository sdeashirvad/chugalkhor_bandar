package com.chugalkhorbandar.application.context;

import java.util.EnumSet;
import java.util.Set;

public final class ContextSectionPriorities {

    private ContextSectionPriorities() {}

    public static int priority(ContextSectionType type) {
        return switch (type) {
            case PERSONALITY -> 10;
            case PROMPT_RULES -> 20;
            case WORLD_CANON -> 30;
            case WORLD_STATE -> 40;
            case CURRENT_CHARACTER -> 50;
            case CURRENT_LOCATION -> 60;
            case RELATIONSHIPS -> 70;
            case WORKING_MEMORY -> 12;
            case CURRENT_CONVERSATION -> 80;
            case SESSION_SUMMARY -> 90;
            case RELEVANT_STORIES -> 100;
            case PUBLIC_EVENTS -> 110;
            case LONG_TERM_MEMORY -> 120;
            case SECRET_MEMORY -> 130;
            case UNKNOWN -> 999;
        };
    }

    public static Set<ContextSectionType> alwaysIncluded() {
        return EnumSet.of(
                ContextSectionType.PERSONALITY,
                ContextSectionType.WORLD_CANON,
                ContextSectionType.CURRENT_CONVERSATION);
    }
}
