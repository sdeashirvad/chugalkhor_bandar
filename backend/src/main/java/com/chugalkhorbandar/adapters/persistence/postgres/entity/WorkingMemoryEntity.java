package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "working_memory")
public class WorkingMemoryEntity {

    @Id
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "active_topic", nullable = false)
    private String activeTopic;

    @Column(name = "conversation_mood", nullable = false)
    private String conversationMood;

    @Column(name = "current_story")
    private String currentStory;

    @Column(name = "active_entities", nullable = false, columnDefinition = "TEXT")
    private String activeEntitiesJson;

    @Column(name = "unanswered_questions", nullable = false, columnDefinition = "TEXT")
    private String unansweredQuestionsJson;

    @Column(name = "recent_promises", nullable = false, columnDefinition = "TEXT")
    private String recentPromisesJson;

    @Column(name = "important_facts", nullable = false, columnDefinition = "TEXT")
    private String importantFactsJson;

    @Column(name = "field_traces", nullable = false, columnDefinition = "TEXT")
    private String fieldTracesJson;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Column(name = "version", nullable = false)
    private long version;

    protected WorkingMemoryEntity() {}

    public WorkingMemoryEntity(
            String sessionId,
            String activeTopic,
            String conversationMood,
            String currentStory,
            String activeEntitiesJson,
            String unansweredQuestionsJson,
            String recentPromisesJson,
            String importantFactsJson,
            String fieldTracesJson,
            Instant lastUpdated,
            long version) {
        this.sessionId = sessionId;
        this.activeTopic = activeTopic;
        this.conversationMood = conversationMood;
        this.currentStory = currentStory;
        this.activeEntitiesJson = activeEntitiesJson;
        this.unansweredQuestionsJson = unansweredQuestionsJson;
        this.recentPromisesJson = recentPromisesJson;
        this.importantFactsJson = importantFactsJson;
        this.fieldTracesJson = fieldTracesJson;
        this.lastUpdated = lastUpdated;
        this.version = version;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getActiveTopic() {
        return activeTopic;
    }

    public String getConversationMood() {
        return conversationMood;
    }

    public String getCurrentStory() {
        return currentStory;
    }

    public String getActiveEntitiesJson() {
        return activeEntitiesJson;
    }

    public String getUnansweredQuestionsJson() {
        return unansweredQuestionsJson;
    }

    public String getRecentPromisesJson() {
        return recentPromisesJson;
    }

    public String getImportantFactsJson() {
        return importantFactsJson;
    }

    public String getFieldTracesJson() {
        return fieldTracesJson;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public long getVersion() {
        return version;
    }
}
