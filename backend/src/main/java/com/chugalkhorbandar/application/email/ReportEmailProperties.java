package com.chugalkhorbandar.application.email;

import com.chugalkhorbandar.application.reporting.RecipientParser;
import com.chugalkhorbandar.config.DotEnvLoader;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "memory-report")
public class ReportEmailProperties {

    private boolean enabled = false;
    private String apiKey = "";
    private String from = "";
    private String to = "";
    private String subjectTemplate = "";

    @PostConstruct
    void applyDotEnvOverrides() {
        Map<String, String> dotEnv = DotEnvLoader.load();
        apply(dotEnv, "MEMORY_REPORT_ENABLED", value -> this.enabled = Boolean.parseBoolean(value));
        apply(dotEnv, "RESEND_API_KEY", value -> this.apiKey = value);
        apply(dotEnv, "RESEND_FROM", value -> this.from = value);
        apply(dotEnv, "MEMORY_REPORT_TO", value -> this.to = value);
        apply(dotEnv, "MEMORY_REPORT_SUBJECT", value -> this.subjectTemplate = value);
    }

    private static void apply(Map<String, String> dotEnv, String key, java.util.function.Consumer<String> setter) {
        String value = dotEnv.get(key);
        if (value != null && !value.isBlank()) {
            setter.accept(value.trim());
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }

    public boolean isConfigured() {
        return enabled
                && apiKey != null
                && !apiKey.isBlank()
                && from != null
                && !from.isBlank()
                && !RecipientParser.parse(to).isEmpty();
    }
}
