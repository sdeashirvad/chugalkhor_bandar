package com.chugalkhorbandar.application.memory.consolidation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.memory-consolidation")
public class MemoryConsolidationProperties {

    private boolean enabled = true;
    private String schedule = "0 0 0 * * *";
    private boolean reflectionEnabled = false;
    private boolean emailEnabled = false;
    private boolean developerManualRun = true;
    private MemoryConsolidationPromotionThresholds promotionThresholds = new MemoryConsolidationPromotionThresholds();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public boolean isReflectionEnabled() {
        return reflectionEnabled;
    }

    public void setReflectionEnabled(boolean reflectionEnabled) {
        this.reflectionEnabled = reflectionEnabled;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isDeveloperManualRun() {
        return developerManualRun;
    }

    public void setDeveloperManualRun(boolean developerManualRun) {
        this.developerManualRun = developerManualRun;
    }

    public MemoryConsolidationPromotionThresholds getPromotionThresholds() {
        return promotionThresholds;
    }

    public void setPromotionThresholds(MemoryConsolidationPromotionThresholds promotionThresholds) {
        this.promotionThresholds = promotionThresholds;
    }
}
