package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeCanon(String id, String title, Map<String, String> sections) {

    public RuntimeCanon {
        sections = Map.copyOf(sections);
    }
}
