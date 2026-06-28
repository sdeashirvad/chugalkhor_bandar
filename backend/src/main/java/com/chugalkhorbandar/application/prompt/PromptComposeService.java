package com.chugalkhorbandar.application.prompt;

import com.chugalkhorbandar.application.context.ContextPlanner;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextRequestFactory;
import com.chugalkhorbandar.application.context.resolver.ContextResolver;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import org.springframework.stereotype.Service;

@Service
public class PromptComposeService {

    private final ContextRequestFactory requestFactory;
    private final ContextPlanner contextPlanner;
    private final ContextResolver contextResolver;
    private final PromptComposer promptComposer;

    public PromptComposeService(
            ContextRequestFactory requestFactory,
            ContextPlanner contextPlanner,
            ContextResolver contextResolver,
            PromptComposer promptComposer) {
        this.requestFactory = requestFactory;
        this.contextPlanner = contextPlanner;
        this.contextResolver = contextResolver;
        this.promptComposer = promptComposer;
    }

    public ComposedPrompt compose(String sessionId, String latestMessage) {
        ContextPlannerRequest plannerRequest = requestFactory.create(sessionId, latestMessage);
        ResolvedContext resolvedContext = contextResolver.resolve(contextPlanner.plan(plannerRequest), plannerRequest);
        PromptComposeRequest composeRequest = new PromptComposeRequest(
                resolvedContext,
                latestMessage,
                plannerRequest.currentCharacter(),
                plannerRequest.session(),
                plannerRequest.currentConversation());
        return promptComposer.compose(composeRequest);
    }

    public PromptInspection inspect(String sessionId, String latestMessage) {
        ContextPlannerRequest plannerRequest = requestFactory.create(sessionId, latestMessage);
        ResolvedContext resolvedContext = contextResolver.resolve(contextPlanner.plan(plannerRequest), plannerRequest);
        PromptComposeRequest composeRequest = new PromptComposeRequest(
                resolvedContext,
                latestMessage,
                plannerRequest.currentCharacter(),
                plannerRequest.session(),
                plannerRequest.currentConversation());
        return promptComposer.inspect(composeRequest);
    }
}
