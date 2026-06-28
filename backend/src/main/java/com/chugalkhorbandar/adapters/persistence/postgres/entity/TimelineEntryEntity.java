package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "timeline_entries")
public class TimelineEntryEntity {

    @Id
    private String id;

    @Column(name = "chronology_id")
    private String chronologyId;

    private String title;

    @Column(name = "timeline_entries", columnDefinition = "TEXT")
    private String timelineEntries;

    @Column(columnDefinition = "TEXT")
    private String sections;

    @Column(name = "recorded_at")
    private Instant recordedAt;

    public TimelineEntryEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChronologyId() {
        return chronologyId;
    }

    public void setChronologyId(String chronologyId) {
        this.chronologyId = chronologyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimelineEntries() {
        return timelineEntries;
    }

    public void setTimelineEntries(String timelineEntries) {
        this.timelineEntries = timelineEntries;
    }

    public String getSections() {
        return sections;
    }

    public void setSections(String sections) {
        this.sections = sections;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }
}
