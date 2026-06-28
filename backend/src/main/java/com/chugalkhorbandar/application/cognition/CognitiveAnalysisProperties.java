package com.chugalkhorbandar.application.cognition;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.cognitive-analysis")
public class CognitiveAnalysisProperties {

    private boolean enabled = true;
    private String provider = "mock";
    private double temperature = 0.1;
    private int timeoutSeconds = 60;
    private double minimumConfidence = 0.5;
    private boolean runAsynchronously = true;
    private boolean mockEnabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public double getMinimumConfidence() {
        return minimumConfidence;
    }

    public void setMinimumConfidence(double minimumConfidence) {
        this.minimumConfidence = minimumConfidence;
    }

    public boolean isRunAsynchronously() {
        return runAsynchronously;
    }

    public void setRunAsynchronously(boolean runAsynchronously) {
        this.runAsynchronously = runAsynchronously;
    }

    public boolean isMockEnabled() {
        return mockEnabled;
    }

    public void setMockEnabled(boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
    }
}
