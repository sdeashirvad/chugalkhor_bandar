package com.chugalkhorbandar.application.memory.working;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.working-memory")
public class WorkingMemoryProperties {

    private int conversationWindowMessages = 10;
    private int analysisWindowMessages = 20;

    public int getConversationWindowMessages() {
        return conversationWindowMessages;
    }

    public void setConversationWindowMessages(int conversationWindowMessages) {
        this.conversationWindowMessages = conversationWindowMessages;
    }

    public int getAnalysisWindowMessages() {
        return analysisWindowMessages;
    }

    public void setAnalysisWindowMessages(int analysisWindowMessages) {
        this.analysisWindowMessages = analysisWindowMessages;
    }
}
