package com.chugalkhorbandar.application.prompt;

import java.util.List;

public record PromptInspection(
        List<PromptInspectionEntry> sections, int totalEstimatedTokens, int requiredSectionCount, int optionalSectionCount) {

    public static PromptInspection from(ComposedPrompt composed) {
        List<PromptInspectionEntry> entries = composed.sections().stream()
                .map(section -> new PromptInspectionEntry(
                        section.sectionType().name(),
                        section.title(),
                        section.priority(),
                        section.required(),
                        section.estimatedTokens()))
                .toList();
        return new PromptInspection(
                entries,
                composed.totalEstimatedTokens(),
                composed.requiredSections().size(),
                composed.optionalSections().size());
    }

    public record PromptInspectionEntry(
            String sectionType, String title, int priority, boolean required, int estimatedTokens) {}
}
