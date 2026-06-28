package com.chugalkhorbandar.application.reporting;

public record ReportingDeliverySummary(String status, String error, int recipientsSent, int recipientsFailed) {

    public ReportingDeliverySummary {
        status = status == null ? "SKIPPED" : status;
        error = error == null ? "" : error;
    }
}
