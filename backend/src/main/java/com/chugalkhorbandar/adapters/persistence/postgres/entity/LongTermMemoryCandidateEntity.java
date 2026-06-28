package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "long_term_memory_candidates")
public class LongTermMemoryCandidateEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "run_id", nullable = false)
    private String runId;

    @Column(name = "owner_character_id", nullable = false)
    private String ownerCharacterId;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "importance", nullable = false)
    private String importance;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "source_inbox_item_ids_json", nullable = false, columnDefinition = "TEXT")
    private String sourceInboxItemIdsJson;

    @Column(name = "metadata_json", nullable = false, columnDefinition = "TEXT")
    private String metadataJson;

    protected LongTermMemoryCandidateEntity() {}

    public LongTermMemoryCandidateEntity(
            String id,
            String runId,
            String ownerCharacterId,
            String summary,
            String importance,
            String reason,
            Instant createdAt,
            String sourceInboxItemIdsJson,
            String metadataJson) {
        this.id = id;
        this.runId = runId;
        this.ownerCharacterId = ownerCharacterId;
        this.summary = summary;
        this.importance = importance;
        this.reason = reason;
        this.createdAt = createdAt;
        this.sourceInboxItemIdsJson = sourceInboxItemIdsJson;
        this.metadataJson = metadataJson;
    }

    public String getId() {
        return id;
    }

    public String getRunId() {
        return runId;
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

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getSourceInboxItemIdsJson() {
        return sourceInboxItemIdsJson;
    }

    public String getMetadataJson() {
        return metadataJson;
    }
}
