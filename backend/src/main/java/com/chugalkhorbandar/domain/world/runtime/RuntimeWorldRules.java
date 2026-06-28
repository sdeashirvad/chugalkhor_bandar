package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeWorldRules(String id, String title, Map<String, String> sections) {

    public RuntimeWorldRules {
        sections = Map.copyOf(sections);
    }
}
