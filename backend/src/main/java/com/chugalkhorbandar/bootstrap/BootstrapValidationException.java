package com.chugalkhorbandar.bootstrap;

import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationReport;

public class BootstrapValidationException extends RuntimeException {

    private final ValidationReport report;

    public BootstrapValidationException(ValidationReport report) {
        super(buildMessage(report));
        this.report = report;
    }

    public ValidationReport getReport() {
        return report;
    }

    private static String buildMessage(ValidationReport report) {
        StringBuilder message = new StringBuilder("Bootstrap validation failed with ");
        message.append(report.errorCount()).append(" error(s).");
        for (ValidationIssue issue : report.issues()) {
            if (issue.severity().name().equals("ERROR")) {
                message.append("\n  - ").append(issue.message());
                issue.filePath().ifPresent(path -> message.append(" (").append(path).append(")"));
            }
        }
        return message.toString();
    }
}
