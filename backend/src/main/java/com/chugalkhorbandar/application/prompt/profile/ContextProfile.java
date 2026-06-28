package com.chugalkhorbandar.application.prompt.profile;

import com.chugalkhorbandar.application.prompt.PromptSectionType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ContextProfile(
        ContextProfileType type,
        String displayName,
        String description,
        Set<PromptSectionType> preferredSections,
        Set<PromptSectionType> optionalSections,
        Set<PromptSectionType> minimumRequiredSections,
        Set<PromptSectionType> reducedSections,
        Map<PromptSectionType, Integer> sectionPriorities) {

    public ContextProfile {
        preferredSections = Set.copyOf(preferredSections);
        optionalSections = Set.copyOf(optionalSections);
        minimumRequiredSections = Set.copyOf(minimumRequiredSections);
        reducedSections = Set.copyOf(reducedSections);
        sectionPriorities = Map.copyOf(sectionPriorities);
    }

    public int priorityFor(PromptSectionType sectionType) {
        return sectionPriorities.getOrDefault(sectionType, Integer.MAX_VALUE);
    }

    public boolean isPreferred(PromptSectionType sectionType) {
        return preferredSections.contains(sectionType);
    }

    public boolean isReduced(PromptSectionType sectionType) {
        return reducedSections.contains(sectionType);
    }

    public boolean isMinimumRequired(PromptSectionType sectionType) {
        return minimumRequiredSections.contains(sectionType);
    }

    public List<PromptSectionType> orderedSectionTypes() {
        return sectionPriorities.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();
    }
}
