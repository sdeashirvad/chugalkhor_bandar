package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "world_tick_history")
public class WorldTickHistoryEntity {

    @Id
    @Column(name = "run_id", nullable = false)
    private String runId;

    @Column(name = "mode", nullable = false)
    private String mode;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "world_date", nullable = false)
    private LocalDate worldDate;

    @Column(name = "events_generated", nullable = false)
    private int eventsGenerated;

    @Column(name = "artifacts_generated", nullable = false)
    private int artifactsGenerated;

    @Column(name = "notifications_generated", nullable = false)
    private int notificationsGenerated;

    @Column(name = "generator_names_json", nullable = false, columnDefinition = "TEXT")
    private String generatorNamesJson;

    @Column(name = "event_ids_json", nullable = false, columnDefinition = "TEXT")
    private String eventIdsJson;

    @Column(name = "artifact_ids_json", nullable = false, columnDefinition = "TEXT")
    private String artifactIdsJson;

    @Column(name = "notification_ids_json", nullable = false, columnDefinition = "TEXT")
    private String notificationIdsJson;

    protected WorldTickHistoryEntity() {}

    public WorldTickHistoryEntity(
            String runId,
            String mode,
            Instant startedAt,
            Instant completedAt,
            long durationMs,
            LocalDate worldDate,
            int eventsGenerated,
            int artifactsGenerated,
            int notificationsGenerated,
            String generatorNamesJson,
            String eventIdsJson,
            String artifactIdsJson,
            String notificationIdsJson) {
        this.runId = runId;
        this.mode = mode;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationMs = durationMs;
        this.worldDate = worldDate;
        this.eventsGenerated = eventsGenerated;
        this.artifactsGenerated = artifactsGenerated;
        this.notificationsGenerated = notificationsGenerated;
        this.generatorNamesJson = generatorNamesJson;
        this.eventIdsJson = eventIdsJson;
        this.artifactIdsJson = artifactIdsJson;
        this.notificationIdsJson = notificationIdsJson;
    }

    public String getRunId() {
        return runId;
    }

    public String getMode() {
        return mode;
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

    public LocalDate getWorldDate() {
        return worldDate;
    }

    public int getEventsGenerated() {
        return eventsGenerated;
    }

    public int getArtifactsGenerated() {
        return artifactsGenerated;
    }

    public int getNotificationsGenerated() {
        return notificationsGenerated;
    }

    public String getGeneratorNamesJson() {
        return generatorNamesJson;
    }

    public String getEventIdsJson() {
        return eventIdsJson;
    }

    public String getArtifactIdsJson() {
        return artifactIdsJson;
    }

    public String getNotificationIdsJson() {
        return notificationIdsJson;
    }
}
