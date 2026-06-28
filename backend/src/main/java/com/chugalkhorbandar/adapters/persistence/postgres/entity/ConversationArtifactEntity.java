package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "conversation_artifacts")
public class ConversationArtifactEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "owner_character_id", nullable = false)
    private String ownerCharacterId;

    @Column(name = "recipient_character_id", nullable = false)
    private String recipientCharacterId;

    @Column(name = "created_by_character_id", nullable = false)
    private String createdByCharacterId;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "metadata_json", nullable = false, columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "trace_json", nullable = false, columnDefinition = "TEXT")
    private String traceJson;

    protected ConversationArtifactEntity() {}

    public ConversationArtifactEntity(
            String id,
            String type,
            String ownerCharacterId,
            String recipientCharacterId,
            String createdByCharacterId,
            String conversationId,
            String title,
            String summary,
            String status,
            String priority,
            Instant createdAt,
            Instant updatedAt,
            Instant expiresAt,
            String metadataJson,
            String traceJson) {
        this.id = id;
        this.type = type;
        this.ownerCharacterId = ownerCharacterId;
        this.recipientCharacterId = recipientCharacterId;
        this.createdByCharacterId = createdByCharacterId;
        this.conversationId = conversationId;
        this.title = title;
        this.summary = summary;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.metadataJson = metadataJson;
        this.traceJson = traceJson;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getOwnerCharacterId() {
        return ownerCharacterId;
    }

    public String getRecipientCharacterId() {
        return recipientCharacterId;
    }

    public String getCreatedByCharacterId() {
        return createdByCharacterId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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
}
