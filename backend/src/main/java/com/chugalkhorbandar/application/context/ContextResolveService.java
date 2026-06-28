package com.chugalkhorbandar.application.context;

import com.chugalkhorbandar.application.context.resolver.ContextResolver;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import org.springframework.stereotype.Service;

@Service
public class ContextResolveService {

    private final ContextRequestFactory requestFactory;
    private final ContextPlanner contextPlanner;
    private final ContextResolver contextResolver;

    public ContextResolveService(
            ContextRequestFactory requestFactory,
            ContextPlanner contextPlanner,
            ContextResolver contextResolver) {
        this.requestFactory = requestFactory;
        this.contextPlanner = contextPlanner;
        this.contextResolver = contextResolver;
    }

    public ContextResolveResult resolve(String sessionId, String latestMessage) {
        ContextPlannerRequest request = requestFactory.create(sessionId, latestMessage);
        ContextPlan plan = contextPlanner.plan(request);
        ResolvedContext resolvedContext = contextResolver.resolve(plan, request);
        return new ContextResolveResult(plan, resolvedContext);
    }

    public ResolvedContext resolveContext(String sessionId, String latestMessage) {
        return resolve(sessionId, latestMessage).resolvedContext();
    }
}
