package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeRelationship(String id, String title, Map<String, String> sections) {

    public RuntimeRelationship {
        sections = Map.copyOf(sections);
    }
}
