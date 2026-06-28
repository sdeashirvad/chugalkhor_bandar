package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.ComposedPromptResponseDto;
import com.chugalkhorbandar.adapters.api.dto.LlmPromptMessageDto;
import com.chugalkhorbandar.adapters.api.dto.PromptInspectionDto;
import com.chugalkhorbandar.adapters.api.dto.PromptInspectionEntryDto;
import com.chugalkhorbandar.adapters.api.dto.PromptSectionDto;
import com.chugalkhorbandar.application.llm.ProviderMessage;
import com.chugalkhorbandar.application.llm.PromptToProviderAdapter;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptInspection;
import com.chugalkhorbandar.application.prompt.PromptSection;

public final class PromptComposeDtoMapper {

    private PromptComposeDtoMapper() {}

    public static ComposedPromptResponseDto toDto(ComposedPrompt composed, PromptToProviderAdapter providerAdapter) {
        PromptInspection inspection = PromptInspection.from(composed);
        return new ComposedPromptResponseDto(
                composed.sections().stream().map(PromptComposeDtoMapper::toDto).toList(),
                composed.totalEstimatedTokens(),
                composed.requiredSections().size(),
                composed.optionalSections().size(),
                toDto(inspection),
                providerAdapter.adapt(composed).messages().stream().map(PromptComposeDtoMapper::toDto).toList());
    }

    private static LlmPromptMessageDto toDto(ProviderMessage message) {
        return new LlmPromptMessageDto(message.role().name().toLowerCase(), message.content());
    }

    private static PromptSectionDto toDto(PromptSection section) {
        return new PromptSectionDto(
                section.sectionType().name(),
                section.title(),
                section.priority(),
                section.required(),
                section.estimatedTokens(),
                section.content(),
                section.fragmentId(),
                section.fragmentType().name());
    }

    private static PromptInspectionDto toDto(PromptInspection inspection) {
        return new PromptInspectionDto(
                inspection.sections().stream().map(PromptComposeDtoMapper::toDto).toList(),
                inspection.totalEstimatedTokens(),
                inspection.requiredSectionCount(),
                inspection.optionalSectionCount());
    }

    private static PromptInspectionEntryDto toDto(PromptInspection.PromptInspectionEntry entry) {
        return new PromptInspectionEntryDto(
                entry.sectionType(),
                entry.title(),
                entry.priority(),
                entry.required(),
                entry.estimatedTokens());
    }
}
