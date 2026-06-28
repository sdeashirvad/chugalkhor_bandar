package com.chugalkhorbandar.application.reporting;

import java.time.Instant;

public record PendingDeliveryRetry(
        String reportId,
        String recipient,
        int nextAttempt,
        Instant retryAt) {}
