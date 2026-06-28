package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeStory(String id, String title, Map<String, String> sections, Map<String, String> linkedStories) {

    public RuntimeStory {
        sections = Map.copyOf(sections);
        linkedStories = Map.copyOf(linkedStories);
    }

    public RuntimeStory withLinkedStory(String linkedStoryId, String linkType) {
        Map<String, String> updated = new java.util.LinkedHashMap<>(linkedStories);
        updated.put(linkedStoryId, linkType);
        return new RuntimeStory(id, title, sections, Map.copyOf(updated));
    }
}
