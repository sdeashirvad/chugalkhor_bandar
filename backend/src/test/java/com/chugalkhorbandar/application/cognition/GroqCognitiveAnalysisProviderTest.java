package com.chugalkhorbandar.application.cognition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.application.llm.groq.GroqHttpClient;
import com.chugalkhorbandar.application.llm.groq.GroqKeyPool;
import com.chugalkhorbandar.config.LlmProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroqCognitiveAnalysisProviderTest {

    @Mock
    private GroqKeyPool keyPool;

    @Mock
    private GroqHttpClient httpClient;

    private GroqCognitiveAnalysisProvider provider;

    @BeforeEach
    void setUp() {
        CognitiveAnalysisProperties properties = new CognitiveAnalysisProperties();
        LlmProperties llmProperties = new LlmProperties();
        provider = new GroqCognitiveAnalysisProvider(keyPool, httpClient, properties, llmProperties);
    }

    @Test
    void returnsParsedJsonFromGroqResponse() throws Exception {
        when(keyPool.keyCount()).thenReturn(1);
        when(keyPool.acquireKey()).thenReturn(new GroqKeyPool.GroqKeySelection(1, "key-1"));
        when(httpClient.chatCompletion(any(), any()))
                .thenReturn(new GroqHttpClient.GroqChatCompletionResult(
                        """
                        {
                          "observations": [
                            {"type": "INTEREST", "confidence": 0.9, "summary": "Interested in stories", "evidence": "story"}
                          ],
                          "recommendations": [
                            {"action": "WAIT", "confidence": 0.8, "reason": "Observe", "target": "conv-1"}
                          ]
                        }
                        """,
                        "stop",
                        10,
                        20,
                        30));

        CognitiveAnalysisProviderResponse response =
                provider.analyzeConversation(MockCognitiveAnalysisProviderTestSupport.sampleInput());

        assertThat(response.provider()).isEqualTo("groq");
        assertThat(response.rawJson()).contains("INTEREST");
    }
}
