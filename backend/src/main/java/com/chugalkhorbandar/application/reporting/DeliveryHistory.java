package com.chugalkhorbandar.application.reporting;

import java.time.Instant;

public record DeliveryHistory(
        String id,
        String reportId,
        String recipient,
        String status,
        String provider,
        String providerMessageId,
        int attempt,
        long latencyMs,
        String error,
        Instant createdAt) {

    public DeliveryHistory {
        status = status == null ? "PENDING" : status;
        provider = provider == null ? "resend" : provider;
        providerMessageId = providerMessageId == null ? "" : providerMessageId;
        error = error == null ? "" : error;
    }
}
