package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "world_events")
public class WorldEventEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "participants_json", nullable = false, columnDefinition = "TEXT")
    private String participantsJson;

    @Column(name = "visibility", nullable = false)
    private String visibility;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "metadata_json", nullable = false, columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "origin", nullable = false)
    private String origin;

    protected WorldEventEntity() {}

    public WorldEventEntity(
            String id,
            String type,
            String title,
            String summary,
            String participantsJson,
            String visibility,
            Instant createdAt,
            LocalDate effectiveDate,
            String metadataJson,
            String status,
            String origin) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.summary = summary;
        this.participantsJson = participantsJson;
        this.visibility = visibility;
        this.createdAt = createdAt;
        this.effectiveDate = effectiveDate;
        this.metadataJson = metadataJson;
        this.status = status;
        this.origin = origin;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getParticipantsJson() {
        return participantsJson;
    }

    public String getVisibility() {
        return visibility;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public String getStatus() {
        return status;
    }

    public String getOrigin() {
        return origin;
    }
}
