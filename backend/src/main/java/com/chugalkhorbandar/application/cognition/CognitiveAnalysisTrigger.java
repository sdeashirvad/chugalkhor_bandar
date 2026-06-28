package com.chugalkhorbandar.application.cognition;

import com.chugalkhorbandar.domain.conversation.Conversation;
import org.springframework.stereotype.Component;

@Component
public class CognitiveAnalysisTrigger {

    private final CognitiveAnalysisService cognitiveAnalysisService;
    private final CognitiveAnalysisAsyncRunner asyncRunner;
    private final CognitiveAnalysisProperties properties;

    public CognitiveAnalysisTrigger(
            CognitiveAnalysisService cognitiveAnalysisService,
            CognitiveAnalysisAsyncRunner asyncRunner,
            CognitiveAnalysisProperties properties) {
        this.cognitiveAnalysisService = cognitiveAnalysisService;
        this.asyncRunner = asyncRunner;
        this.properties = properties;
    }

    public void schedule(String sessionId, Conversation conversation, String latestUserMessage) {
        if (!properties.isEnabled()) {
            return;
        }
        if (properties.isRunAsynchronously()) {
            asyncRunner.run(sessionId, conversation, latestUserMessage);
            return;
        }
        cognitiveAnalysisService.analyzeCompletedTurn(sessionId, conversation, latestUserMessage);
    }
}
