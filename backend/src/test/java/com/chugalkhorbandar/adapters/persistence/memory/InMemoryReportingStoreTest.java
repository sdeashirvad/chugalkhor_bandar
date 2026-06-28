package com.chugalkhorbandar.adapters.persistence.memory;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.reporting.DeliveryHistory;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class InMemoryReportingStoreTest {

    @Test
    void findFailedEligibleForRetryKeepsLatestAttemptPerRecipient() {
        InMemoryReportingStore store = new InMemoryReportingStore();
        Instant now = Instant.parse("2026-06-01T06:00:00Z");
        store.saveHistory(new DeliveryHistory(
                "h-1", "run-1", "a@gmail.com", "FAILED", "resend", "", 1, 5, "fail", now));
        store.saveHistory(new DeliveryHistory(
                "h-2", "run-1", "a@gmail.com", "FAILED", "resend", "", 2, 8, "fail", now.plusSeconds(60)));

        assertThat(store.findFailedEligibleForRetry(3)).hasSize(1);
        assertThat(store.findFailedEligibleForRetry(3).get(0).attempt()).isEqualTo(2);
    }
}
