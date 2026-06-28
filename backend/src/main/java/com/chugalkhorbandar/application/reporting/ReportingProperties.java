package com.chugalkhorbandar.application.reporting;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.reporting")
public class ReportingProperties {

    private boolean enabled = true;
    private boolean archiveEnabled = true;
    private boolean retryEnabled = true;
    private boolean previewEnabled = true;
    private int maxRetries = 3;
    private String subjectTemplate = "🐒 Bandar's Morning Letter — {date}";
    private String closingStrategy = "deterministic";
    private List<String> closings = defaultClosings();
    private ReportingBrandingProperties branding = new ReportingBrandingProperties();
    private ReportingAttachmentProperties attachments = new ReportingAttachmentProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isArchiveEnabled() {
        return archiveEnabled;
    }

    public void setArchiveEnabled(boolean archiveEnabled) {
        this.archiveEnabled = archiveEnabled;
    }

    public boolean isRetryEnabled() {
        return retryEnabled;
    }

    public void setRetryEnabled(boolean retryEnabled) {
        this.retryEnabled = retryEnabled;
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        this.previewEnabled = previewEnabled;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }

    public String getClosingStrategy() {
        return closingStrategy;
    }

    public void setClosingStrategy(String closingStrategy) {
        this.closingStrategy = closingStrategy;
    }

    public List<String> getClosings() {
        return closings;
    }

    public void setClosings(List<String> closings) {
        this.closings = closings == null ? defaultClosings() : List.copyOf(closings);
    }

    public ReportingBrandingProperties getBranding() {
        return branding;
    }

    public void setBranding(ReportingBrandingProperties branding) {
        this.branding = branding;
    }

    public ReportingAttachmentProperties getAttachments() {
        return attachments;
    }

    public void setAttachments(ReportingAttachmentProperties attachments) {
        this.attachments = attachments;
    }

    private static List<String> defaultClosings() {
        return List.of(
                "Every creature has a story.",
                "Until tomorrow.",
                "Sleep well. Tomorrow the Jungle wakes again.",
                "There are always more stories.");
    }
}
