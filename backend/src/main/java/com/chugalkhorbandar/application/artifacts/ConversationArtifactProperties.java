package com.chugalkhorbandar.application.artifacts;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chugalkhor.artifacts")
public class ConversationArtifactProperties {

    private int artifactExpirationDays = 30;
    private int maximumActiveArtifacts = 20;

    public int getArtifactExpirationDays() {
        return artifactExpirationDays;
    }

    public void setArtifactExpirationDays(int artifactExpirationDays) {
        this.artifactExpirationDays = artifactExpirationDays;
    }

    public int getMaximumActiveArtifacts() {
        return maximumActiveArtifacts;
    }

    public void setMaximumActiveArtifacts(int maximumActiveArtifacts) {
        this.maximumActiveArtifacts = maximumActiveArtifacts;
    }
}
