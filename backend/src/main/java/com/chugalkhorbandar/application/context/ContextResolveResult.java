package com.chugalkhorbandar.application.context;

import com.chugalkhorbandar.application.context.resolver.ResolvedContext;

public record ContextResolveResult(ContextPlan plan, ResolvedContext resolvedContext) {}
