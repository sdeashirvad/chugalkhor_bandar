package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.ContextPlanResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ContextPlanningTraceDto;
import com.chugalkhorbandar.adapters.api.dto.ContextPlanningTraceEntryDto;
import com.chugalkhorbandar.adapters.api.dto.ContextReferenceDto;
import com.chugalkhorbandar.adapters.api.dto.ContextSectionDto;
import com.chugalkhorbandar.adapters.api.dto.KnowledgeFragmentDto;
import com.chugalkhorbandar.adapters.api.dto.KnowledgeFragmentTraceEntryDto;
import com.chugalkhorbandar.adapters.api.dto.ResolvedContextResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ResolvedContextSectionDto;
import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlanningTraceEntry;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextResolveResult;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlan;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentSelectionEntry;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import java.util.Map;
import java.util.stream.Collectors;

public final class ContextPlanDtoMapper {

    private ContextPlanDtoMapper() {}

    public static ContextPlanResponseDto toDto(ContextPlan plan) {
        return new ContextPlanResponseDto(
                plan.sections().stream().map(ContextPlanDtoMapper::toDto).toList(),
                plan.totalEstimatedTokens(),
                new ContextPlanningTraceDto(plan.trace().entries().stream()
                        .map(ContextPlanDtoMapper::toDto)
                        .toList()),
                plan.fragmentPlan().trace().entries().stream()
                        .map(ContextPlanDtoMapper::toDto)
                        .toList());
    }

    public static ResolvedContextResponseDto toDto(ContextResolveResult result) {
        Map<String, String> reasons = result.plan().fragmentPlan().trace().entries().stream()
                .collect(Collectors.toMap(
                        entry -> entry.fragmentType().name(),
                        KnowledgeFragmentSelectionEntry::reason,
                        (left, right) -> left));
        return new ResolvedContextResponseDto(
                result.resolvedContext().sections().stream().map(ContextPlanDtoMapper::toDto).toList(),
                result.resolvedContext().fragments().stream()
                        .map(fragment -> toDto(fragment, reasons.getOrDefault(fragment.fragmentType().name(), "")))
                        .toList(),
                result.resolvedContext().totalEstimatedTokens());
    }

    public static ResolvedContextResponseDto toDto(ResolvedContext resolved, KnowledgeFragmentPlan fragmentPlan) {
        Map<String, String> reasons = fragmentPlan.trace().entries().stream()
                .collect(Collectors.toMap(
                        entry -> entry.fragmentType().name(),
                        KnowledgeFragmentSelectionEntry::reason,
                        (left, right) -> left));
        return new ResolvedContextResponseDto(
                resolved.sections().stream().map(ContextPlanDtoMapper::toDto).toList(),
                resolved.fragments().stream()
                        .map(fragment -> toDto(fragment, reasons.getOrDefault(fragment.fragmentType().name(), "")))
                        .toList(),
                resolved.totalEstimatedTokens());
    }

    private static KnowledgeFragmentDto toDto(KnowledgeFragment fragment, String selectionReason) {
        return new KnowledgeFragmentDto(
                fragment.fragmentId(),
                fragment.fragmentType().name(),
                fragment.title(),
                fragment.content(),
                fragment.sourceDocument(),
                fragment.sourceSection(),
                fragment.estimatedTokens(),
                fragment.tags(),
                fragment.confidence(),
                selectionReason);
    }

    private static KnowledgeFragmentTraceEntryDto toDto(KnowledgeFragmentSelectionEntry entry) {
        return new KnowledgeFragmentTraceEntryDto(entry.fragmentType().name(), entry.reason());
    }

    private static ContextSectionDto toDto(ContextSection section) {
        return new ContextSectionDto(
                section.type().name(),
                section.priority(),
                section.source(),
                section.contentReference(),
                section.estimatedTokens(),
                toDto(section.reference()));
    }

    private static ResolvedContextSectionDto toDto(ResolvedContextSection section) {
        return new ResolvedContextSectionDto(
                section.type().name(),
                section.priority(),
                section.source(),
                toDto(section.reference()),
                section.contentReference(),
                section.content(),
                section.estimatedTokens());
    }

    private static ContextReferenceDto toDto(ContextReference reference) {
        return new ContextReferenceDto(
                reference.provider(),
                reference.entityType(),
                reference.entityId(),
                reference.attribute(),
                reference.priority());
    }

    private static ContextPlanningTraceEntryDto toDto(ContextPlanningTraceEntry entry) {
        return new ContextPlanningTraceEntryDto(entry.type().name(), entry.reason());
    }
}
