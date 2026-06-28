package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.BudgetedPromptSectionDto;
import com.chugalkhorbandar.adapters.api.dto.ContextProfileDto;
import com.chugalkhorbandar.adapters.api.dto.DroppedSectionDto;
import com.chugalkhorbandar.adapters.api.dto.PromptBudgetDto;
import com.chugalkhorbandar.adapters.api.dto.PromptBudgetResponseDto;
import com.chugalkhorbandar.adapters.api.dto.PromptProfileResponseDto;
import com.chugalkhorbandar.adapters.api.dto.PromptSectionDto;
import com.chugalkhorbandar.adapters.api.dto.ProviderCapabilitiesDto;
import com.chugalkhorbandar.adapters.api.dto.SectionBudgetDto;
import com.chugalkhorbandar.application.llm.ProviderCapabilities;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.application.prompt.budget.BudgetedPrompt;
import com.chugalkhorbandar.application.prompt.budget.BudgetedPromptSection;
import com.chugalkhorbandar.application.prompt.budget.DroppedSection;
import com.chugalkhorbandar.application.prompt.budget.PromptBudget;
import com.chugalkhorbandar.application.prompt.budget.PromptBudgetResult;
import com.chugalkhorbandar.application.prompt.budget.SectionBudget;
import com.chugalkhorbandar.application.prompt.profile.ContextProfile;
import com.chugalkhorbandar.application.prompt.profile.PromptProfileResult;
import java.util.Map;
import java.util.stream.Collectors;

public final class PromptProfileDtoMapper {

    private PromptProfileDtoMapper() {}

    public static PromptProfileResponseDto toDto(PromptProfileResult result) {
        return new PromptProfileResponseDto(
                toDto(result.selection().profile()), result.selection().reason());
    }

    public static PromptBudgetResponseDto toDto(PromptBudgetResult result) {
        BudgetedPrompt budgetedPrompt = result.budgetedPrompt();
        return new PromptBudgetResponseDto(
                toDto(result.profileSelection().profile()),
                result.profileSelection().reason(),
                budgetedPrompt.sections().stream().map(PromptProfileDtoMapper::toDto).toList(),
                budgetedPrompt.droppedSections().stream().map(PromptProfileDtoMapper::toDto).toList(),
                toDto(budgetedPrompt.budget()),
                budgetedPrompt.totalPromptTokens(),
                budgetedPrompt.remainingBudget(),
                toDto(result.capabilities()));
    }

    private static ContextProfileDto toDto(ContextProfile profile) {
        return new ContextProfileDto(
                profile.type().name(),
                profile.displayName(),
                profile.description(),
                profile.preferredSections().stream().map(Enum::name).sorted().toList(),
                profile.optionalSections().stream().map(Enum::name).sorted().toList(),
                profile.minimumRequiredSections().stream().map(Enum::name).sorted().toList(),
                profile.reducedSections().stream().map(Enum::name).sorted().toList(),
                profile.sectionPriorities().entrySet().stream()
                        .collect(Collectors.toMap(entry -> entry.getKey().name(), Map.Entry::getValue)));
    }

    private static BudgetedPromptSectionDto toDto(BudgetedPromptSection section) {
        return new BudgetedPromptSectionDto(
                toDto(section.section()), toDto(section.budget()), section.truncated(), section.allocatedTokens());
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

    private static SectionBudgetDto toDto(SectionBudget budget) {
        return new SectionBudgetDto(
                budget.sectionType().name(),
                budget.maxTokens(),
                budget.minimumTokens(),
                budget.priority(),
                budget.required());
    }

    private static DroppedSectionDto toDto(DroppedSection section) {
        return new DroppedSectionDto(
                section.sectionType().name(),
                section.title(),
                section.estimatedTokens(),
                section.reason());
    }

    private static PromptBudgetDto toDto(PromptBudget budget) {
        return new PromptBudgetDto(
                budget.sectionBudgets().stream().map(PromptProfileDtoMapper::toDto).toList(),
                budget.totalAvailableTokens(),
                budget.reservedOutputTokens(),
                budget.maxContextTokens());
    }

    private static ProviderCapabilitiesDto toDto(ProviderCapabilities capabilities) {
        return new ProviderCapabilitiesDto(
                capabilities.maxContextTokens(),
                capabilities.reservedOutputTokens(),
                capabilities.availablePromptTokens(),
                capabilities.supportsSystemMessages(),
                capabilities.supportsMultiMessage());
    }
}
