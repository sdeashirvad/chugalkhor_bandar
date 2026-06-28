package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeLaw(String id, String title, Map<String, String> sections) {

    public RuntimeLaw {
        sections = Map.copyOf(sections);
    }
}
