package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "memory_inbox_items")
public class MemoryInboxItemEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "source_id", nullable = false)
    private String sourceId;

    @Column(name = "owner_character_id", nullable = false)
    private String ownerCharacterId;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "importance", nullable = false)
    private String importance;

    @Column(name = "confidence", nullable = false)
    private double confidence;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "metadata_json", nullable = false, columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "trace_json", nullable = false, columnDefinition = "TEXT")
    private String traceJson;

    @Column(name = "analysis_id", nullable = false)
    private String analysisId;

    @Column(name = "artifact_ids_json", nullable = false, columnDefinition = "TEXT")
    private String artifactIdsJson;

    protected MemoryInboxItemEntity() {}

    public MemoryInboxItemEntity(
            String id,
            String type,
            String source,
            String sourceId,
            String ownerCharacterId,
            String summary,
            String importance,
            double confidence,
            String status,
            Instant createdAt,
            Instant expiresAt,
            String metadataJson,
            String traceJson,
            String analysisId,
            String artifactIdsJson) {
        this.id = id;
        this.type = type;
        this.source = source;
        this.sourceId = sourceId;
        this.ownerCharacterId = ownerCharacterId;
        this.summary = summary;
        this.importance = importance;
        this.confidence = confidence;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.metadataJson = metadataJson;
        this.traceJson = traceJson;
        this.analysisId = analysisId;
        this.artifactIdsJson = artifactIdsJson;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getOwnerCharacterId() {
        return ownerCharacterId;
    }

    public String getSummary() {
        return summary;
    }

    public String getImportance() {
        return importance;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public String getTraceJson() {
        return traceJson;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public String getArtifactIdsJson() {
        return artifactIdsJson;
    }
}
