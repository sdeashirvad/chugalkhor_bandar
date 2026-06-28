package com.chugalkhorbandar.bootstrap.model;

import java.util.List;

public record ValidationReport(
        boolean manifestValid,
        int characterCount,
        int storyCount,
        int promptCount,
        int chronologyCount,
        int warningCount,
        int errorCount,
        ValidationStatus status,
        List<ValidationIssue> issues) {

    public boolean isValid() {
        return status == ValidationStatus.VALID;
    }

    public String toSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Bootstrap Validation Report\n\n");
        summary.append("Manifest\n");
        summary.append(manifestValid ? "✓\n\n" : "✗\n\n");
        summary.append("Characters\n").append(characterCount).append("\n\n");
        summary.append("Stories\n").append(storyCount).append("\n\n");
        summary.append("Prompts\n").append(promptCount).append("\n\n");
        summary.append("Warnings\n").append(warningCount).append("\n\n");
        summary.append("Errors\n").append(errorCount).append("\n\n");
        summary.append("Status\n").append(status).append("\n");
        return summary.toString();
    }
}
