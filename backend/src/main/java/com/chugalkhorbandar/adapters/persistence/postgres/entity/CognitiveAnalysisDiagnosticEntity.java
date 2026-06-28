package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "cognitive_analysis_diagnostics")
public class CognitiveAnalysisDiagnosticEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "character_id", nullable = false)
    private String characterId;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "error_message", nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_time_ms", nullable = false)
    private long executionTimeMs;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CognitiveAnalysisDiagnosticEntity() {}

    public CognitiveAnalysisDiagnosticEntity(
            String id,
            String characterId,
            String conversationId,
            String provider,
            String errorMessage,
            long executionTimeMs,
            Instant createdAt) {
        this.id = id;
        this.characterId = characterId;
        this.conversationId = conversationId;
        this.provider = provider;
        this.errorMessage = errorMessage;
        this.executionTimeMs = executionTimeMs;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getCharacterId() {
        return characterId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getProvider() {
        return provider;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
