package com.chugalkhorbandar.application.cognition;

import com.chugalkhorbandar.domain.conversation.Conversation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CognitiveAnalysisAsyncRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CognitiveAnalysisAsyncRunner.class);

    private final CognitiveAnalysisService cognitiveAnalysisService;

    public CognitiveAnalysisAsyncRunner(CognitiveAnalysisService cognitiveAnalysisService) {
        this.cognitiveAnalysisService = cognitiveAnalysisService;
    }

    @Async("cognitiveAnalysisExecutor")
    public void run(String sessionId, Conversation conversation, String latestUserMessage) {
        try {
            cognitiveAnalysisService.analyzeCompletedTurn(sessionId, conversation, latestUserMessage);
        } catch (Exception exception) {
            LOGGER.error(
                    "Unexpected cognitive analysis async failure for conversation {}",
                    conversation.conversationId(),
                    exception);
        }
    }
}
