package com.chugalkhorbandar.application.context.knowledge;

import java.util.List;

public record KnowledgeFragmentPlanningTrace(List<KnowledgeFragmentSelectionEntry> entries) {

    public KnowledgeFragmentPlanningTrace {
        entries = List.copyOf(entries);
    }
}
