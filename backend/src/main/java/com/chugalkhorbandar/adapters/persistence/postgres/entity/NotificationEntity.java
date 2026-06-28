package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "recipient_character_id", nullable = false)
    private String recipientCharacterId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "trigger_name", nullable = false)
    private String triggerName;

    @Column(name = "metadata_json", nullable = false, columnDefinition = "TEXT")
    private String metadataJson;

    protected NotificationEntity() {}

    public NotificationEntity(
            String id,
            String recipientCharacterId,
            String type,
            String priority,
            String title,
            String summary,
            String status,
            Instant createdAt,
            Instant expiresAt,
            String source,
            String triggerName,
            String metadataJson) {
        this.id = id;
        this.recipientCharacterId = recipientCharacterId;
        this.type = type;
        this.priority = priority;
        this.title = title;
        this.summary = summary;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.source = source;
        this.triggerName = triggerName;
        this.metadataJson = metadataJson;
    }

    public String getId() {
        return id;
    }

    public String getRecipientCharacterId() {
        return recipientCharacterId;
    }

    public String getType() {
        return type;
    }

    public String getPriority() {
        return priority;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getSource() {
        return source;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public String getMetadataJson() {
        return metadataJson;
    }
}
