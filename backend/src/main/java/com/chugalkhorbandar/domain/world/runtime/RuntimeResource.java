package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

public record RuntimeResource(String id, String title, Map<String, String> sections, int availableQuantity) {

    public RuntimeResource {
        sections = Map.copyOf(sections);
    }

    public RuntimeResource withAvailableQuantity(int quantity) {
        return new RuntimeResource(id, title, sections, quantity);
    }
}
