package com.chugalkhorbandar.application.context.knowledge;

public final class KnowledgeFragmentPriorities {

    private KnowledgeFragmentPriorities() {}

    public static int priority(KnowledgeFragmentType type) {
        return switch (type) {
            case IDENTITY -> 1;
            case SPEAKING_STYLE -> 15;
            case PERSONALITY -> 18;
            case STORYTELLING -> 20;
            case HUMOR -> 22;
            case SECRET_POLICY -> 24;
            case CHARACTER_OPINION -> 26;
            case RELATIONSHIP_TO_BANDAR -> 10;
            case WORLD_GEOGRAPHY -> 30;
            case WORLD_HISTORY -> 35;
            case WORLD_POLITICS -> 40;
            case WORLD_SPECIES -> 45;
            case WORLD_ECONOMY -> 50;
            case WORLD_TRANSPORT -> 55;
            case CHARACTER_PROFILE -> 46;
            case CHARACTER_LOCATION -> 48;
            case CHARACTER_TITLES -> 50;
            case CHARACTER_RELATIONSHIPS -> 52;
            case CHARACTER_PREFERENCES -> 54;
            case STORY_SUMMARY -> 100;
            case TIMELINE -> 110;
            case CONVERSATION -> 85;
            case WORKING_MEMORY -> 12;
            case UNKNOWN -> 999;
        };
    }
}
