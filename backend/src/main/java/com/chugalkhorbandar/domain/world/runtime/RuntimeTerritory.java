package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeTerritory(String id, String title, Map<String, String> sections, String currentRulerId) {

    public RuntimeTerritory {
        sections = Map.copyOf(sections);
    }

    public RuntimeTerritory withCurrentRulerId(String rulerId) {
        return new RuntimeTerritory(id, title, sections, rulerId);
    }
}
