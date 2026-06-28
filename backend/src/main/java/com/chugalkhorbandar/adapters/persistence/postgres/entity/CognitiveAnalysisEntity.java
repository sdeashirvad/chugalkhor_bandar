package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "cognitive_analyses")
public class CognitiveAnalysisEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "character_id", nullable = false)
    private String characterId;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "latency_ms", nullable = false)
    private long latencyMs;

    @Column(name = "confidence", nullable = false)
    private double confidence;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "raw_json", nullable = false, columnDefinition = "TEXT")
    private String rawJson;

    @Column(name = "observations_json", nullable = false, columnDefinition = "TEXT")
    private String observationsJson;

    @Column(name = "recommendations_json", nullable = false, columnDefinition = "TEXT")
    private String recommendationsJson;

    protected CognitiveAnalysisEntity() {}

    public CognitiveAnalysisEntity(
            String id,
            String characterId,
            String conversationId,
            String provider,
            String model,
            long latencyMs,
            double confidence,
            Instant createdAt,
            String rawJson,
            String observationsJson,
            String recommendationsJson) {
        this.id = id;
        this.characterId = characterId;
        this.conversationId = conversationId;
        this.provider = provider;
        this.model = model;
        this.latencyMs = latencyMs;
        this.confidence = confidence;
        this.createdAt = createdAt;
        this.rawJson = rawJson;
        this.observationsJson = observationsJson;
        this.recommendationsJson = recommendationsJson;
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

    public String getModel() {
        return model;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public double getConfidence() {
        return confidence;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getRawJson() {
        return rawJson;
    }

    public String getObservationsJson() {
        return observationsJson;
    }

    public String getRecommendationsJson() {
        return recommendationsJson;
    }
}
