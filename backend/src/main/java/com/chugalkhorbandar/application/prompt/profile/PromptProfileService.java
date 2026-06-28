package com.chugalkhorbandar.application.prompt.profile;

import com.chugalkhorbandar.application.prompt.PromptPipelineResult;
import com.chugalkhorbandar.application.prompt.PromptPipelineService;
import org.springframework.stereotype.Service;

@Service
public class PromptProfileService {

    private final PromptPipelineService promptPipelineService;
    private final ContextProfileSelector contextProfileSelector;

    public PromptProfileService(
            PromptPipelineService promptPipelineService, ContextProfileSelector contextProfileSelector) {
        this.promptPipelineService = promptPipelineService;
        this.contextProfileSelector = contextProfileSelector;
    }

    public PromptProfileResult selectProfile(String sessionId, String latestMessage) {
        PromptPipelineResult pipeline = promptPipelineService.run(sessionId, latestMessage);
        ProfileSelection selection = contextProfileSelector.select(
                pipeline.plannerRequest(), pipeline.plan(), pipeline.resolvedContext());
        return new PromptProfileResult(selection, pipeline);
    }
}
