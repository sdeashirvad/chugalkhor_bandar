package com.chugalkhorbandar.application.context.knowledge;

import java.util.List;

public record KnowledgeFragmentPlan(
        List<KnowledgeFragmentRequest> requests,
        int totalEstimatedTokens,
        KnowledgeFragmentPlanningTrace trace) {

    public KnowledgeFragmentPlan {
        requests = List.copyOf(requests);
    }
}
