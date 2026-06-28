package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeOrganization(
        String id, String title, Map<String, String> sections, Map<String, String> roles) {

    public RuntimeOrganization {
        sections = Map.copyOf(sections);
        roles = Map.copyOf(roles);
    }

    public RuntimeOrganization withRole(String characterId, String role) {
        Map<String, String> updated = new java.util.LinkedHashMap<>(roles);
        updated.put(characterId, role);
        return new RuntimeOrganization(id, title, sections, Map.copyOf(updated));
    }
}
