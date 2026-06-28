package com.chugalkhorbandar.adapters.api.dto;

import java.util.List;
import java.util.Map;

public record ContextProfileDto(
        String type,
        String displayName,
        String description,
        List<String> preferredSections,
        List<String> optionalSections,
        List<String> minimumRequiredSections,
        List<String> reducedSections,
        Map<String, Integer> sectionPriorities) {}
