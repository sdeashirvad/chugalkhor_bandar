package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeObject(String id, String title, Map<String, String> sections, String ownerId) {

    public RuntimeObject {
        sections = Map.copyOf(sections);
    }

    public RuntimeObject withOwnerId(String newOwnerId) {
        return new RuntimeObject(id, title, sections, newOwnerId);
    }
}
