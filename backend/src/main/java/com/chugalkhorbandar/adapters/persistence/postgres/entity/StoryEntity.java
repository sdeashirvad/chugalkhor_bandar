package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stories")
public class StoryEntity {

    @Id
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String sections;

    @Column(name = "linked_stories", columnDefinition = "TEXT")
    private String linkedStories;

    public StoryEntity() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSections() {
        return sections;
    }

    public void setSections(String sections) {
        this.sections = sections;
    }

    public String getLinkedStories() {
        return linkedStories;
    }

    public void setLinkedStories(String linkedStories) {
        this.linkedStories = linkedStories;
    }
}
