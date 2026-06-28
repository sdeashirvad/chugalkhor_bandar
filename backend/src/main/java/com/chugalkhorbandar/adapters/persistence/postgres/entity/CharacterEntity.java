package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "characters")
public class CharacterEntity {

    @Id
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String sections;

    @Column(name = "current_place_id")
    private String currentPlaceId;

    @Column(columnDefinition = "TEXT")
    private String preferences;

    public CharacterEntity() {}

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

    public String getCurrentPlaceId() {
        return currentPlaceId;
    }

    public void setCurrentPlaceId(String currentPlaceId) {
        this.currentPlaceId = currentPlaceId;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
