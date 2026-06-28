package com.chugalkhorbandar.application.context;

public record ContextReference(
        String provider, String entityType, String entityId, String attribute, int priority) {

    public String format() {
        if (entityId == null || entityId.isBlank()) {
            return entityType;
        }
        if (attribute == null || attribute.isBlank()) {
            return entityType + ":" + entityId;
        }
        return entityType + ":" + entityId + ":" + attribute;
    }
}
