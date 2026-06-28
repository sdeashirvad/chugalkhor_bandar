package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "chronicles")
public class ChronicleEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "visibility", nullable = false)
    private String visibility;

    @Column(name = "confidence", nullable = false)
    private String confidence;

    @Column(name = "owner_character_id", nullable = false)
    private String ownerCharacterId;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "chronicle_date", nullable = false)
    private LocalDate chronicleDate;

    @Column(name = "metadata_json", nullable = false, columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "provenance_json", nullable = false, columnDefinition = "TEXT")
    private String provenanceJson;

    @Column(name = "version", nullable = false)
    private int version;

    protected ChronicleEntity() {}

    public ChronicleEntity(
            String id,
            String title,
            String category,
            String visibility,
            String confidence,
            String ownerCharacterId,
            String summary,
            String body,
            Instant createdAt,
            LocalDate chronicleDate,
            String metadataJson,
            String provenanceJson,
            int version) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.visibility = visibility;
        this.confidence = confidence;
        this.ownerCharacterId = ownerCharacterId;
        this.summary = summary;
        this.body = body;
        this.createdAt = createdAt;
        this.chronicleDate = chronicleDate;
        this.metadataJson = metadataJson;
        this.provenanceJson = provenanceJson;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getConfidence() {
        return confidence;
    }

    public String getOwnerCharacterId() {
        return ownerCharacterId;
    }

    public String getSummary() {
        return summary;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public LocalDate getChronicleDate() {
        return chronicleDate;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public String getProvenanceJson() {
        return provenanceJson;
    }

    public int getVersion() {
        return version;
    }
}
