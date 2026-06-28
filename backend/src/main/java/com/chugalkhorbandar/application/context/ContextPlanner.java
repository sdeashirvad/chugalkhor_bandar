package com.chugalkhorbandar.application.context;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentLegacyAdapter;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlan;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlanner;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentSelectionEntry;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ContextPlanner {

    private final KnowledgeFragmentPlanner knowledgeFragmentPlanner;

    public ContextPlanner(KnowledgeFragmentPlanner knowledgeFragmentPlanner) {
        this.knowledgeFragmentPlanner = knowledgeFragmentPlanner;
    }

    public ContextPlan plan(ContextPlannerRequest request) {
        KnowledgeFragmentPlan fragmentPlan = knowledgeFragmentPlanner.plan(request);
        List<ContextSection> sections = new java.util.ArrayList<>(KnowledgeFragmentLegacyAdapter.toContextSections(fragmentPlan));
        sections.sort(Comparator.comparingInt(ContextSection::priority).thenComparing(section -> section.type().name()));

        List<ContextPlanningTraceEntry> traceEntries = fragmentPlan.trace().entries().stream()
                .sorted(Comparator.comparingInt(entry -> ContextSectionPriorities.priority(
                        KnowledgeFragmentLegacyAdapter.toContextSectionType(entry.fragmentType()))))
                .map(entry -> new ContextPlanningTraceEntry(
                        KnowledgeFragmentLegacyAdapter.toContextSectionType(entry.fragmentType()),
                        formatTraceReason(entry)))
                .distinct()
                .toList();

        int totalTokens = fragmentPlan.requests().stream()
                .mapToInt(requestItem -> ContextSection.estimateTokens(requestItem.reference()))
                .sum();
        return new ContextPlan(
                List.copyOf(sections),
                fragmentPlan,
                totalTokens,
                new ContextPlanningTrace(traceEntries));
    }

    private static String formatTraceReason(KnowledgeFragmentSelectionEntry entry) {
        return entry.fragmentType().name() + " — " + entry.reason();
    }
}
