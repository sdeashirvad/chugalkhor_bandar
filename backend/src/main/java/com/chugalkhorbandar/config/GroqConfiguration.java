package com.chugalkhorbandar.config;

import com.chugalkhorbandar.application.llm.groq.GroqHttpClient;
import com.chugalkhorbandar.application.llm.groq.GroqKeyPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class GroqConfiguration {

@Bean
    GroqKeyPool groqKeyPool(GroqProperties groqProperties) {
        return new GroqKeyPool(List.of(groqProperties.getApiKey1(), groqProperties.getApiKey2()));
    }

    @Bean(name = "groqHttpClient")
    GroqHttpClient groqHttpClient(
            ObjectMapper objectMapper, GroqProperties groqProperties, LlmProperties llmProperties, Environment environment) {
        return new GroqHttpClient(
                objectMapper,
                groqProperties.getBaseUrl(),
                llmProperties.getTimeoutSeconds(),
                environment);
    }
}
