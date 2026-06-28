package com.chugalkhorbandar.ports;

public enum PersistenceProviderType {
    IN_MEMORY_H2("InMemory (H2)"),
    POSTGRESQL("PostgreSQL");

    private final String displayName;

    PersistenceProviderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
