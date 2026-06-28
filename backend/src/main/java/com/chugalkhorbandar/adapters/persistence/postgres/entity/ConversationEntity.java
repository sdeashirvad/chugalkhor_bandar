package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "conversations")
public class ConversationEntity {

    @Id
    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "character_id", nullable = false)
    private String characterId;

    @Column(name = "character_display_name", nullable = false)
    private String characterDisplayName;

    @Column(name = "character_titles", nullable = false, columnDefinition = "TEXT")
    private String characterTitles;

    @Column(name = "character_species")
    private String characterSpecies;

    @Column(name = "character_home_territory")
    private String characterHomeTerritory;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "last_activity", nullable = false)
    private Instant lastActivity;

    @Column(nullable = false)
    private String status;

    public ConversationEntity() {}

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getCharacterDisplayName() {
        return characterDisplayName;
    }

    public void setCharacterDisplayName(String characterDisplayName) {
        this.characterDisplayName = characterDisplayName;
    }

    public String getCharacterTitles() {
        return characterTitles;
    }

    public void setCharacterTitles(String characterTitles) {
        this.characterTitles = characterTitles;
    }

    public String getCharacterSpecies() {
        return characterSpecies;
    }

    public void setCharacterSpecies(String characterSpecies) {
        this.characterSpecies = characterSpecies;
    }

    public String getCharacterHomeTerritory() {
        return characterHomeTerritory;
    }

    public void setCharacterHomeTerritory(String characterHomeTerritory) {
        this.characterHomeTerritory = characterHomeTerritory;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Instant lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
