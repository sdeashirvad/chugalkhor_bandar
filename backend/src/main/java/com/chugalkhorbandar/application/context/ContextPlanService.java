package com.chugalkhorbandar.application.context;

import org.springframework.stereotype.Service;

@Service
public class ContextPlanService {

    private final ContextRequestFactory requestFactory;
    private final ContextPlanner contextPlanner;

    public ContextPlanService(ContextRequestFactory requestFactory, ContextPlanner contextPlanner) {
        this.requestFactory = requestFactory;
        this.contextPlanner = contextPlanner;
    }

    public ContextPlan plan(String sessionId, String latestMessage) {
        return contextPlanner.plan(requestFactory.create(sessionId, latestMessage));
    }
}
