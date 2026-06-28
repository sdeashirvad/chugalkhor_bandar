package com.chugalkhorbandar.application.context;

import java.util.List;

public record RuntimeWorldContext(
        String status, String bootstrapVersion, int characterCount, int storyCount, List<String> knownEntityLabels) {

    public RuntimeWorldContext {
        knownEntityLabels = List.copyOf(knownEntityLabels == null ? List.of() : knownEntityLabels);
    }
}
