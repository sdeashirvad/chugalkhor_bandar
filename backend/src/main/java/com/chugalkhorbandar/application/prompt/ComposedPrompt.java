package com.chugalkhorbandar.application.prompt;

import java.util.List;

public record ComposedPrompt(List<PromptSection> sections) {

    public ComposedPrompt {
        sections = List.copyOf(sections);
    }

    public int totalEstimatedTokens() {
        return sections.stream().mapToInt(PromptSection::estimatedTokens).sum();
    }

    public List<PromptSection> requiredSections() {
        return sections.stream().filter(PromptSection::required).toList();
    }

    public List<PromptSection> optionalSections() {
        return sections.stream().filter(section -> !section.required()).toList();
    }
}
