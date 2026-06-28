package com.chugalkhorbandar.application.memory.consolidation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.memory-consolidation.promotion-thresholds")
public class MemoryConsolidationPromotionThresholds {

    private int preferenceRepeatCount = 2;
    private int storySeedRepeatCount = 2;
    private double minimumConfidence = 0.5;

    public int getPreferenceRepeatCount() {
        return preferenceRepeatCount;
    }

    public void setPreferenceRepeatCount(int preferenceRepeatCount) {
        this.preferenceRepeatCount = preferenceRepeatCount;
    }

    public int getStorySeedRepeatCount() {
        return storySeedRepeatCount;
    }

    public void setStorySeedRepeatCount(int storySeedRepeatCount) {
        this.storySeedRepeatCount = storySeedRepeatCount;
    }

    public double getMinimumConfidence() {
        return minimumConfidence;
    }

    public void setMinimumConfidence(double minimumConfidence) {
        this.minimumConfidence = minimumConfidence;
    }
}
