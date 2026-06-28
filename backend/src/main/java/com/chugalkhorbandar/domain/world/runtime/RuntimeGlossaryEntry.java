package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeGlossaryEntry(String id, String title, Map<String, String> sections) {

    public RuntimeGlossaryEntry {
        sections = Map.copyOf(sections);
    }
}
