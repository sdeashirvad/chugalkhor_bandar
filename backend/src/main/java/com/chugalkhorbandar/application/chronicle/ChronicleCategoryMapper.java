package com.chugalkhorbandar.application.chronicle;

import java.util.Locale;
import java.util.Map;

public final class ChronicleCategoryMapper {

    private ChronicleCategoryMapper() {}

    public static ChronicleCategory fromCandidateType(String type) {
        if (type == null || type.isBlank()) {
            return ChronicleCategory.CUSTOM;
        }
        return switch (type.toUpperCase(Locale.ROOT)) {
            case "PROMISE" -> ChronicleCategory.PROMISE;
            case "PROMOTE_TO_MEMORY" -> ChronicleCategory.PERSONAL;
            case "PREFERENCE" -> ChronicleCategory.PREFERENCE;
            case "STORY_SEED" -> ChronicleCategory.STORY;
            case "OPEN_QUESTION" -> ChronicleCategory.DISCOVERY;
            case "REMINDER" -> ChronicleCategory.EVENT;
            case "RELATIONSHIP" -> ChronicleCategory.RELATIONSHIP;
            case "WORLD" -> ChronicleCategory.WORLD;
            default -> ChronicleCategory.CUSTOM;
        };
    }

    public static ChronicleCategory fromMetadata(Map<String, String> metadata) {
        return fromCandidateType(metadata.getOrDefault("type", ""));
    }
}
