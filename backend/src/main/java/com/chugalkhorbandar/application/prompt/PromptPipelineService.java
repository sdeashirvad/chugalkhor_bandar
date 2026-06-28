package com.chugalkhorbandar.application.prompt;

import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlanner;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextRequestFactory;
import com.chugalkhorbandar.application.context.resolver.ContextResolver;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import org.springframework.stereotype.Service;

@Service
public class PromptPipelineService {

    private final ContextRequestFactory requestFactory;
    private final ContextPlanner contextPlanner;
    private final ContextResolver contextResolver;
    private final PromptComposer promptComposer;

    public PromptPipelineService(
            ContextRequestFactory requestFactory,
            ContextPlanner contextPlanner,
            ContextResolver contextResolver,
            PromptComposer promptComposer) {
        this.requestFactory = requestFactory;
        this.contextPlanner = contextPlanner;
        this.contextResolver = contextResolver;
        this.promptComposer = promptComposer;
    }

    public PromptPipelineResult run(String sessionId, String latestMessage) {
        ContextPlannerRequest plannerRequest = requestFactory.create(sessionId, latestMessage);
        ContextPlan plan = contextPlanner.plan(plannerRequest);
        ResolvedContext resolvedContext = contextResolver.resolve(plan, plannerRequest);
        PromptComposeRequest composeRequest = new PromptComposeRequest(
                resolvedContext,
                latestMessage,
                plannerRequest.currentCharacter(),
                plannerRequest.session(),
                plannerRequest.currentConversation());
        ComposedPrompt composedPrompt = promptComposer.compose(composeRequest);
        return new PromptPipelineResult(plannerRequest, plan, resolvedContext, composedPrompt);
    }
}
