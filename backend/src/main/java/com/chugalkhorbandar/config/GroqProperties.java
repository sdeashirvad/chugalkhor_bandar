package com.chugalkhorbandar.config;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "groq")
public class GroqProperties {

    private String baseUrl = "https://api.groq.com/openai/v1";
    private String apiKey1 = "";
    private String apiKey2 = "";

    @PostConstruct
    void applyDotEnvOverrides() {
        Map<String, String> dotEnv = DotEnvLoader.load();
        apply(dotEnv, "GROQ_BASE_URL", value -> this.baseUrl = value);
        apply(dotEnv, "GROQ_API_KEY_1", value -> this.apiKey1 = value);
        apply(dotEnv, "GROQ_API_KEY_2", value -> this.apiKey2 = value);
    }

    private static void apply(Map<String, String> dotEnv, String key, java.util.function.Consumer<String> setter) {
        String value = dotEnv.get(key);
        if (value != null && !value.isBlank()) {
            setter.accept(value.trim());
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey1() {
        return apiKey1;
    }

    public void setApiKey1(String apiKey1) {
        this.apiKey1 = apiKey1;
    }

    public String getApiKey2() {
        return apiKey2;
    }

    public void setApiKey2(String apiKey2) {
        this.apiKey2 = apiKey2;
    }
}
