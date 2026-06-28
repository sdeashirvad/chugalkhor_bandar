package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "delivery_history")
public class DeliveryHistoryEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "report_id", nullable = false)
    private String reportId;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_message_id", nullable = false)
    private String providerMessageId;

    @Column(name = "attempt", nullable = false)
    private int attempt;

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs;

    @Column(name = "error", nullable = false, columnDefinition = "TEXT")
    private String error;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected DeliveryHistoryEntity() {}

    public DeliveryHistoryEntity(
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
        this.id = id;
        this.reportId = reportId;
        this.recipient = recipient;
        this.status = status;
        this.provider = provider;
        this.providerMessageId = providerMessageId;
        this.attempt = attempt;
        this.latencyMs = latencyMs;
        this.error = error;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getReportId() {
        return reportId;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getStatus() {
        return status;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public int getAttempt() {
        return attempt;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public String getError() {
        return error;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
