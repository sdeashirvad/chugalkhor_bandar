package com.chugalkhorbandar.application.reporting;

import java.util.List;

public record ReportingEmailPayload(
        String from,
        List<String> recipients,
        String subject,
        String htmlBody,
        List<ReportingAttachment> attachments) {

    public ReportingEmailPayload {
        recipients = List.copyOf(recipients == null ? List.of() : recipients);
        attachments = List.copyOf(attachments == null ? List.of() : attachments);
    }
}
