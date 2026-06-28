package com.chugalkhorbandar.application.prompt;

import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;

public record PromptPipelineResult(
        ContextPlannerRequest plannerRequest,
        ContextPlan plan,
        ResolvedContext resolvedContext,
        ComposedPrompt composedPrompt) {}
