package com.chugalkhorbandar.application.cognition;

public interface CognitiveAnalysisProvider {

    String providerName();

    boolean isAvailable();

    CognitiveAnalysisProviderResponse analyzeConversation(CognitiveAnalysisInput input);
}
