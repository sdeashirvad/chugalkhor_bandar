package com.chugalkhorbandar.application.reporting;

import java.time.Instant;

public record ReportArchive(
        String reportId,
        String htmlContent,
        String txtContent,
        String jsonContent,
        String markdownContent,
        Instant createdAt) {

    public ReportArchive {
        htmlContent = htmlContent == null ? "" : htmlContent;
        txtContent = txtContent == null ? "" : txtContent;
        jsonContent = jsonContent == null ? "" : jsonContent;
        markdownContent = markdownContent == null ? "" : markdownContent;
    }
}
