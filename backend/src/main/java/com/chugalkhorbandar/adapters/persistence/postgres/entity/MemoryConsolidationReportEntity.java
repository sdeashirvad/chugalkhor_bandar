package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "memory_consolidation_reports")
public class MemoryConsolidationReportEntity {

    @Id
    @Column(name = "run_id", nullable = false)
    private String runId;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "processed", nullable = false)
    private int processed;

    @Column(name = "promoted", nullable = false)
    private int promoted;

    @Column(name = "discarded", nullable = false)
    private int discarded;

    @Column(name = "expired", nullable = false)
    private int expired;

    @Column(name = "archived", nullable = false)
    private int archived;

    @Column(name = "pending", nullable = false)
    private int pending;

    @Column(name = "candidate_count", nullable = false)
    private int candidateCount;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "txt_report", nullable = false, columnDefinition = "TEXT")
    private String txtReport;

    @Column(name = "json_report", nullable = false, columnDefinition = "TEXT")
    private String jsonReport;

    @Column(name = "reflection", nullable = false, columnDefinition = "TEXT")
    private String reflection;

    @Column(name = "email_status", nullable = false)
    private String emailStatus;

    @Column(name = "email_error", nullable = false, columnDefinition = "TEXT")
    private String emailError;

    protected MemoryConsolidationReportEntity() {}

    public MemoryConsolidationReportEntity(
            String runId,
            Instant startedAt,
            Instant completedAt,
            long durationMs,
            int processed,
            int promoted,
            int discarded,
            int expired,
            int archived,
            int pending,
            int candidateCount,
            String summary,
            String txtReport,
            String jsonReport,
            String reflection,
            String emailStatus,
            String emailError) {
        this.runId = runId;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMs = durationMs;
        this.processed = processed;
        this.promoted = promoted;
        this.discarded = discarded;
        this.expired = expired;
        this.archived = archived;
        this.pending = pending;
        this.candidateCount = candidateCount;
        this.summary = summary;
        this.txtReport = txtReport;
        this.jsonReport = jsonReport;
        this.reflection = reflection;
        this.emailStatus = emailStatus;
        this.emailError = emailError;
    }

    public String getRunId() {
        return runId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public int getProcessed() {
        return processed;
    }

    public int getPromoted() {
        return promoted;
    }

    public int getDiscarded() {
        return discarded;
    }

    public int getExpired() {
        return expired;
    }

    public int getArchived() {
        return archived;
    }

    public int getPending() {
        return pending;
    }

    public int getCandidateCount() {
        return candidateCount;
    }

    public String getSummary() {
        return summary;
    }

    public String getTxtReport() {
        return txtReport;
    }

    public String getJsonReport() {
        return jsonReport;
    }

    public String getReflection() {
        return reflection;
    }

    public String getEmailStatus() {
        return emailStatus;
    }

    public String getEmailError() {
        return emailError;
    }
}
