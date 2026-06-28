package com.chugalkhorbandar.application.memory.inbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.memory-inbox")
public class MemoryInboxProperties {

    private boolean enabled = true;
    private double minimumConfidence = 0.5;
    private int defaultExpirationDays = 30;
    private boolean deduplicationEnabled = true;
    private int maximumItems = 100;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getMinimumConfidence() {
        return minimumConfidence;
    }

    public void setMinimumConfidence(double minimumConfidence) {
        this.minimumConfidence = minimumConfidence;
    }

    public int getDefaultExpirationDays() {
        return defaultExpirationDays;
    }

    public void setDefaultExpirationDays(int defaultExpirationDays) {
        this.defaultExpirationDays = defaultExpirationDays;
    }

    public boolean isDeduplicationEnabled() {
        return deduplicationEnabled;
    }

    public void setDeduplicationEnabled(boolean deduplicationEnabled) {
        this.deduplicationEnabled = deduplicationEnabled;
    }

    public int getMaximumItems() {
        return maximumItems;
    }

    public void setMaximumItems(int maximumItems) {
        this.maximumItems = maximumItems;
    }
}
