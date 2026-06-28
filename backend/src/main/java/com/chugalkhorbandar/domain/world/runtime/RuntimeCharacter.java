package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeCharacter(
        String id,
        String title,
        Map<String, String> sections,
        String currentPlaceId,
        Map<String, String> preferences) {

    public RuntimeCharacter {
        sections = Map.copyOf(sections);
        preferences = Map.copyOf(preferences);
    }

    public RuntimeCharacter withTitle(String newTitle) {
        return new RuntimeCharacter(id, newTitle, sections, currentPlaceId, preferences);
    }

    public RuntimeCharacter withSections(Map<String, String> newSections) {
        return new RuntimeCharacter(id, title, newSections, currentPlaceId, preferences);
    }

    public RuntimeCharacter withCurrentPlaceId(String placeId) {
        return new RuntimeCharacter(id, title, sections, placeId, preferences);
    }

    public RuntimeCharacter withPreference(String key, String value) {
        Map<String, String> updated = new java.util.LinkedHashMap<>(preferences);
        updated.put(key, value);
        return new RuntimeCharacter(id, title, sections, currentPlaceId, Map.copyOf(updated));
    }
}
