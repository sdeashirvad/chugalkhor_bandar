package com.chugalkhorbandar.application.context;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlan;
import java.util.List;

public record ContextPlan(
        List<ContextSection> sections,
        KnowledgeFragmentPlan fragmentPlan,
        int totalEstimatedTokens,
        ContextPlanningTrace trace) {}