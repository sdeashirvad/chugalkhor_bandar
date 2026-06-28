package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimePromptProfile(String id, String title, Map<String, String> sections) {

    public RuntimePromptProfile {
        sections = Map.copyOf(sections);
    }
}
