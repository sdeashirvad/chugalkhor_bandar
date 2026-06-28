package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeCustom(String id, String title, Map<String, String> sections) {

    public RuntimeCustom {
        sections = Map.copyOf(sections);
    }
}
