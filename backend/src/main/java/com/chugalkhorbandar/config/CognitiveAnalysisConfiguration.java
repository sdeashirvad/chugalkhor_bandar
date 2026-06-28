package com.chugalkhorbandar.config;

import com.chugalkhorbandar.application.llm.groq.GroqHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class CognitiveAnalysisConfiguration {

    @Bean(name = "cognitiveAnalysisExecutor")
    Executor cognitiveAnalysisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("cognitive-analysis-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "cognitiveAnalysisGroqHttpClient")
    GroqHttpClient cognitiveAnalysisGroqHttpClient(
            ObjectMapper objectMapper,
            GroqProperties groqProperties,
            com.chugalkhorbandar.application.cognition.CognitiveAnalysisProperties cognitiveAnalysisProperties,
            Environment environment) {
        return new GroqHttpClient(
                objectMapper,
                groqProperties.getBaseUrl(),
                cognitiveAnalysisProperties.getTimeoutSeconds(),
                environment);
    }
}
