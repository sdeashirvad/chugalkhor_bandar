package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "territories")
public class TerritoryEntity {

    @Id
    private String id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String sections;

    @Column(name = "current_ruler_id")
    private String currentRulerId;

    public TerritoryEntity() {}

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

    public String getCurrentRulerId() {
        return currentRulerId;
    }

    public void setCurrentRulerId(String currentRulerId) {
        this.currentRulerId = currentRulerId;
    }
}
