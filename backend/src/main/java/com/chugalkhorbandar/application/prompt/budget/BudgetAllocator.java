package com.chugalkhorbandar.application.prompt.budget;

import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.llm.ProviderCapabilities;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.application.prompt.profile.ContextProfile;
import com.chugalkhorbandar.config.PromptProfileProperties;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BudgetAllocator {

    private static final int PREFERRED_WEIGHT = 3;
    private static final int NORMAL_WEIGHT = 2;
    private static final int REDUCED_WEIGHT = 1;

    private final PromptProfileProperties promptProfileProperties;

    public BudgetAllocator(PromptProfileProperties promptProfileProperties) {
        this.promptProfileProperties = promptProfileProperties;
    }

    public BudgetedPrompt allocate(
            ComposedPrompt composedPrompt, ContextProfile profile, ProviderCapabilities capabilities) {
        int availableBudget = capabilities.availablePromptTokens();
        int minimumSectionTokens = promptProfileProperties.getBudget().getMinimumSectionTokens();

        List<PromptSection> orderedSections = composedPrompt.sections().stream()
                .sorted(Comparator.<PromptSection>comparingInt(section -> sectionPriority(section, profile))
                        .thenComparing(BudgetAllocator::sectionKey))
                .toList();

        Map<String, AllocationPlan> plans = new LinkedHashMap<>();
        for (PromptSection section : orderedSections) {
            boolean required = isRequired(section, profile);
            int priority = sectionPriority(section, profile);
            int weight = weightFor(profile, section);
            plans.put(sectionKey(section), new AllocationPlan(section, required, priority, weight, section.estimatedTokens()));
        }

        int totalEstimated = orderedSections.stream().mapToInt(PromptSection::estimatedTokens).sum();
        List<DroppedSection> droppedSections = new ArrayList<>();

        if (totalEstimated > availableBudget) {
            dropOptionalSections(plans, droppedSections, profile, availableBudget);
        }

        int activeWeight = plans.values().stream().mapToInt(AllocationPlan::weight).sum();
        Map<String, Integer> assignedMaxTokens = new LinkedHashMap<>();
        for (AllocationPlan plan : plans.values()) {
            int proportional = activeWeight == 0
                    ? minimumSectionTokens
                    : Math.max(minimumSectionTokens, (availableBudget * plan.weight()) / activeWeight);
            assignedMaxTokens.put(sectionKey(plan.section()), proportional);
        }

        normalizeBudgetAssignments(assignedMaxTokens, plans, availableBudget, minimumSectionTokens);

        List<BudgetedPromptSection> keptSections = new ArrayList<>();
        List<SectionBudget> sectionBudgets = new ArrayList<>();
        int totalPromptTokens = 0;

        for (PromptSection section : composedPrompt.sections()) {
            AllocationPlan plan = plans.get(sectionKey(section));
            if (plan == null) {
                continue;
            }

            int maxTokens = assignedMaxTokens.getOrDefault(sectionKey(section), minimumSectionTokens);
            int minimumTokens = plan.required() ? minimumSectionTokens : minimumSectionTokens;
            SectionBudget sectionBudget = new SectionBudget(
                    section.sectionType(), maxTokens, minimumTokens, plan.priority(), plan.required());
            sectionBudgets.add(sectionBudget);

            TruncatedSection truncatedSection = truncate(section, maxTokens);
            keptSections.add(new BudgetedPromptSection(
                    truncatedSection.section(),
                    sectionBudget,
                    truncatedSection.truncated(),
                    truncatedSection.allocatedTokens()));
            totalPromptTokens += truncatedSection.allocatedTokens();
        }

        PromptBudget promptBudget = new PromptBudget(
                sectionBudgets,
                availableBudget,
                capabilities.reservedOutputTokens(),
                capabilities.maxContextTokens());

        return new BudgetedPrompt(
                profile.type(),
                keptSections,
                droppedSections,
                promptBudget,
                totalPromptTokens,
                Math.max(0, availableBudget - totalPromptTokens));
    }

    private static void dropOptionalSections(
            Map<String, AllocationPlan> plans,
            List<DroppedSection> droppedSections,
            ContextProfile profile,
            int availableBudget) {
        List<AllocationPlan> droppable = plans.values().stream()
                .filter(plan -> !plan.required())
                .sorted(Comparator
                        .comparing((AllocationPlan plan) -> !profile.isReduced(plan.section().sectionType()))
                        .thenComparing(AllocationPlan::dropPriority, Comparator.reverseOrder()))
                .toList();

        int remainingEstimate = plans.values().stream().mapToInt(AllocationPlan::estimatedTokens).sum();
        for (AllocationPlan candidate : droppable) {
            if (remainingEstimate <= availableBudget) {
                break;
            }
            plans.remove(sectionKey(candidate.section()));
            remainingEstimate -= candidate.estimatedTokens();
            droppedSections.add(new DroppedSection(
                    candidate.section().sectionType(),
                    candidate.section().title(),
                    candidate.estimatedTokens(),
                    dropReason(profile, candidate.section())));
        }
    }

    private static String dropReason(ContextProfile profile, PromptSection section) {
        if (profile.isReduced(section.sectionType())) {
            return "Reduced by profile " + profile.type().name();
        }
        if (section.isFragment()) {
            return "Optional fragment dropped to fit provider budget";
        }
        return "Optional section dropped to fit provider budget";
    }

    private static void normalizeBudgetAssignments(
            Map<String, Integer> assignedMaxTokens,
            Map<String, AllocationPlan> plans,
            int availableBudget,
            int minimumSectionTokens) {
        int assignedTotal = assignedMaxTokens.entrySet().stream()
                .filter(entry -> plans.containsKey(entry.getKey()))
                .mapToInt(Map.Entry::getValue)
                .sum();
        if (assignedTotal <= availableBudget) {
            return;
        }

        List<String> shrinkOrder = assignedMaxTokens.keySet().stream()
                .filter(plans::containsKey)
                .sorted(Comparator.comparingInt(key -> plans.get(key).dropPriority()).reversed())
                .toList();

        int overflow = assignedTotal - availableBudget;
        for (String key : shrinkOrder) {
            if (overflow <= 0) {
                break;
            }
            AllocationPlan plan = plans.get(key);
            int current = assignedMaxTokens.get(key);
            int floor = minimumSectionTokens;
            int reducible = Math.max(0, current - floor);
            int reduction = Math.min(reducible, overflow);
            assignedMaxTokens.put(key, current - reduction);
            overflow -= reduction;
        }
    }

    private static int sectionPriority(PromptSection section, ContextProfile profile) {
        if (section.isFragment()) {
            return section.priority();
        }
        return profile.priorityFor(section.sectionType());
    }

    private static int weightFor(ContextProfile profile, PromptSection section) {
        if (profile.isPreferred(section.sectionType())) {
            return PREFERRED_WEIGHT;
        }
        if (profile.isReduced(section.sectionType())) {
            return REDUCED_WEIGHT;
        }
        return NORMAL_WEIGHT;
    }

    private static String sectionKey(PromptSection section) {
        return section.isFragment() ? section.fragmentId() : section.sectionType().name();
    }

    private static boolean isRequired(PromptSection section, ContextProfile profile) {
        return section.required() || profile.isMinimumRequired(section.sectionType());
    }

    private static TruncatedSection truncate(PromptSection section, int maxTokens) {
        int allocatedTokens = Math.min(section.estimatedTokens(), maxTokens);
        if (section.estimatedTokens() <= maxTokens) {
            return new TruncatedSection(section, false, allocatedTokens);
        }
        int maxCharacters = Math.max(1, maxTokens * 4);
        String truncatedContent = section.content();
        if (truncatedContent.length() > maxCharacters) {
            truncatedContent = truncatedContent.substring(0, Math.max(0, maxCharacters - 3)).stripTrailing() + "...";
        }
        PromptSection updated = PromptSection.fromFragment(
                section.sectionType(),
                section.title(),
                section.priority(),
                section.required(),
                allocatedTokens,
                truncatedContent,
                section.fragmentId(),
                section.fragmentType());
        return new TruncatedSection(updated, true, allocatedTokens);
    }

    private record AllocationPlan(
            PromptSection section, boolean required, int priority, int weight, int estimatedTokens) {

        int dropPriority() {
            if (required) {
                return Integer.MIN_VALUE;
            }
            return priority;
        }
    }

    private record TruncatedSection(PromptSection section, boolean truncated, int allocatedTokens) {}
}
