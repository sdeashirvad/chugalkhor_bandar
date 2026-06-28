package com.chugalkhorbandar.config;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

    private String provider = "mock";
    private String model = "mock-bandar";
    private double temperature = 0.7;
    private int maxOutputTokens = 1024;
    private int timeoutSeconds = 60;

    @PostConstruct
    void applyDotEnvOverrides() {
        Map<String, String> dotEnv = DotEnvLoader.load();
        apply(dotEnv, "LLM_PROVIDER", value -> this.provider = value);
        apply(dotEnv, "GROQ_MODEL", value -> this.model = value);
        apply(dotEnv, "LLM_TEMPERATURE", value -> this.temperature = Double.parseDouble(value));
        apply(dotEnv, "LLM_MAX_OUTPUT_TOKENS", value -> this.maxOutputTokens = Integer.parseInt(value));
        apply(dotEnv, "LLM_TIMEOUT_SECONDS", value -> this.timeoutSeconds = Integer.parseInt(value));
    }

    private static void apply(Map<String, String> dotEnv, String key, java.util.function.Consumer<String> setter) {
        String value = dotEnv.get(key);
        if (value != null && !value.isBlank()) {
            setter.accept(value.trim());
        }
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(int maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
