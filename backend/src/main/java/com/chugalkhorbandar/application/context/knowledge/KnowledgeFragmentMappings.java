package com.chugalkhorbandar.application.context.knowledge;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class KnowledgeFragmentMappings {

    private KnowledgeFragmentMappings() {}

    private static final Map<String, KnowledgeFragmentType> PERSONALITY_SECTIONS = Map.ofEntries(
            Map.entry("identity", KnowledgeFragmentType.IDENTITY),
            Map.entry("corePersonality", KnowledgeFragmentType.PERSONALITY),
            Map.entry("personality", KnowledgeFragmentType.PERSONALITY),
            Map.entry("speakingStyle", KnowledgeFragmentType.SPEAKING_STYLE),
            Map.entry("storytellingStyle", KnowledgeFragmentType.STORYTELLING),
            Map.entry("senseOfHumor", KnowledgeFragmentType.HUMOR),
            Map.entry("secrets", KnowledgeFragmentType.SECRET_POLICY),
            Map.entry("attitudeTowardCharacters", KnowledgeFragmentType.CHARACTER_OPINION));

    private static final Map<String, KnowledgeFragmentType> CHARACTER_SECTIONS = Map.ofEntries(
            Map.entry("summary", KnowledgeFragmentType.CHARACTER_PROFILE),
            Map.entry("roles", KnowledgeFragmentType.CHARACTER_PROFILE),
            Map.entry("personality", KnowledgeFragmentType.CHARACTER_PROFILE),
            Map.entry("titles", KnowledgeFragmentType.CHARACTER_TITLES),
            Map.entry("relationships", KnowledgeFragmentType.CHARACTER_RELATIONSHIPS),
            Map.entry("knownPreferences", KnowledgeFragmentType.CHARACTER_PREFERENCES),
            Map.entry("transportation", KnowledgeFragmentType.WORLD_TRANSPORT));

    private static final Map<String, KnowledgeFragmentType> CANON_SECTIONS = Map.ofEntries(
            Map.entry("theWorld", KnowledgeFragmentType.WORLD_GEOGRAPHY),
            Map.entry("world", KnowledgeFragmentType.WORLD_GEOGRAPHY),
            Map.entry("dynasties", KnowledgeFragmentType.WORLD_HISTORY),
            Map.entry("history", KnowledgeFragmentType.WORLD_HISTORY),
            Map.entry("currentMajorPowers", KnowledgeFragmentType.WORLD_POLITICS),
            Map.entry("politics", KnowledgeFragmentType.WORLD_POLITICS),
            Map.entry("species", KnowledgeFragmentType.WORLD_SPECIES),
            Map.entry("economy", KnowledgeFragmentType.WORLD_ECONOMY),
            Map.entry("transportation", KnowledgeFragmentType.WORLD_TRANSPORT),
            Map.entry("canonicalFacts", KnowledgeFragmentType.WORLD_GEOGRAPHY),
            Map.entry("facts", KnowledgeFragmentType.WORLD_GEOGRAPHY));

    public static KnowledgeFragmentType personalitySectionType(String sectionKey) {
        return PERSONALITY_SECTIONS.getOrDefault(normalizeKey(sectionKey), KnowledgeFragmentType.UNKNOWN);
    }

    public static KnowledgeFragmentType characterSectionType(String sectionKey) {
        return CHARACTER_SECTIONS.getOrDefault(normalizeKey(sectionKey), KnowledgeFragmentType.UNKNOWN);
    }

    public static KnowledgeFragmentType canonSectionType(String sectionKey) {
        return CANON_SECTIONS.getOrDefault(normalizeKey(sectionKey), inferCanonType(sectionKey));
    }

    public static Set<String> tagsFor(KnowledgeFragmentType type, String entityId) {
        return switch (type) {
            case CHARACTER_LOCATION -> Set.of("location", entityTag(entityId));
            case CHARACTER_PROFILE, CHARACTER_TITLES -> Set.of("hippu", "king", entityTag(entityId));
            case CHARACTER_RELATIONSHIPS -> Set.of("friendship", "hippu", entityTag(entityId));
            case CHARACTER_PREFERENCES -> Set.of("food", entityTag(entityId));
            case WORLD_HISTORY -> Set.of("history");
            case WORLD_POLITICS -> Set.of("politics");
            case WORLD_ECONOMY -> Set.of("economy");
            case WORLD_TRANSPORT -> Set.of("transport");
            case WORLD_SPECIES -> Set.of("hippu", "rabbitu", "species");
            case WORLD_GEOGRAPHY -> Set.of("location", "jungle");
            case SECRET_POLICY -> Set.of("secret");
            case RELATIONSHIP_TO_BANDAR -> Set.of("relationship", entityTag(entityId));
            case STORY_SUMMARY -> Set.of("history");
            default -> Set.of();
        };
    }

    private static KnowledgeFragmentType inferCanonType(String sectionKey) {
        String normalized = normalizeKey(sectionKey);
        if (normalized.contains("history") || normalized.contains("dynast")) {
            return KnowledgeFragmentType.WORLD_HISTORY;
        }
        if (normalized.contains("politic") || normalized.contains("power")) {
            return KnowledgeFragmentType.WORLD_POLITICS;
        }
        if (normalized.contains("species") || normalized.contains("rabbitu") || normalized.contains("hippu")) {
            return KnowledgeFragmentType.WORLD_SPECIES;
        }
        if (normalized.contains("econom") || normalized.contains("trade")) {
            return KnowledgeFragmentType.WORLD_ECONOMY;
        }
        if (normalized.contains("transport")) {
            return KnowledgeFragmentType.WORLD_TRANSPORT;
        }
        return KnowledgeFragmentType.UNKNOWN;
    }

    private static String entityTag(String entityId) {
        if (entityId == null || entityId.isBlank()) {
            return "unknown";
        }
        return entityId.replace("character_", "").replace('_', '-');
    }

    private static String normalizeKey(String key) {
        if (key == null) {
            return "";
        }
        String trimmed = key.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.substring(0, 1).toLowerCase(Locale.ROOT) + trimmed.substring(1);
    }
}
