package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimePlace(String id, String title, Map<String, String> sections) {

    public RuntimePlace {
        sections = Map.copyOf(sections);
    }
}
