package com.chugalkhorbandar.application.llm.groq;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.config.GroqProperties;
import com.chugalkhorbandar.config.LlmProperties;
import org.junit.jupiter.api.Test;

class GroqConfigurationTest {

    @Test
    void loadsGroqAndLlmProperties() {
        GroqProperties groqProperties = new GroqProperties();
        groqProperties.setBaseUrl("https://example.test/v1");
        groqProperties.setApiKey1("first-key");
        groqProperties.setApiKey2("second-key");

        LlmProperties llmProperties = new LlmProperties();
        llmProperties.setProvider("groq");
        llmProperties.setModel("llama-test");
        llmProperties.setTemperature(0.5);
        llmProperties.setMaxOutputTokens(512);
        llmProperties.setTimeoutSeconds(30);

        assertThat(groqProperties.getBaseUrl()).isEqualTo("https://example.test/v1");
        assertThat(groqProperties.getApiKey1()).isEqualTo("first-key");
        assertThat(groqProperties.getApiKey2()).isEqualTo("second-key");
        assertThat(llmProperties.getProvider()).isEqualTo("groq");
        assertThat(llmProperties.getTimeoutSeconds()).isEqualTo(30);
    }
}
