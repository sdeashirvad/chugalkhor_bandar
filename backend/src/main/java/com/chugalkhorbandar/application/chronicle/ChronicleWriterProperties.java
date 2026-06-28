package com.chugalkhorbandar.application.chronicle;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.chronicles")
public class ChronicleWriterProperties {

    private boolean enabled = true;
    private boolean writerEnabled = true;
    private ChronicleVisibility defaultVisibility = ChronicleVisibility.PRIVATE;
    private ChronicleConfidence defaultConfidence = ChronicleConfidence.LIKELY;
    private boolean allowTemplateCustomization = false;
    private boolean developerWriteEnabled = true;
    private boolean futureVersioningEnabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isWriterEnabled() {
        return writerEnabled;
    }

    public void setWriterEnabled(boolean writerEnabled) {
        this.writerEnabled = writerEnabled;
    }

    public ChronicleVisibility getDefaultVisibility() {
        return defaultVisibility;
    }

    public void setDefaultVisibility(ChronicleVisibility defaultVisibility) {
        this.defaultVisibility = defaultVisibility;
    }

    public void setDefaultVisibility(String defaultVisibility) {
        this.defaultVisibility = ChronicleVisibility.valueOf(defaultVisibility);
    }

    public ChronicleConfidence getDefaultConfidence() {
        return defaultConfidence;
    }

    public void setDefaultConfidence(ChronicleConfidence defaultConfidence) {
        this.defaultConfidence = defaultConfidence;
    }

    public void setDefaultConfidence(String defaultConfidence) {
        this.defaultConfidence = ChronicleConfidence.valueOf(defaultConfidence);
    }

    public boolean isAllowTemplateCustomization() {
        return allowTemplateCustomization;
    }

    public void setAllowTemplateCustomization(boolean allowTemplateCustomization) {
        this.allowTemplateCustomization = allowTemplateCustomization;
    }

    public boolean isDeveloperWriteEnabled() {
        return developerWriteEnabled;
    }

    public void setDeveloperWriteEnabled(boolean developerWriteEnabled) {
        this.developerWriteEnabled = developerWriteEnabled;
    }

    public boolean isFutureVersioningEnabled() {
        return futureVersioningEnabled;
    }

    public void setFutureVersioningEnabled(boolean futureVersioningEnabled) {
        this.futureVersioningEnabled = futureVersioningEnabled;
    }
}
