package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.adapters.persistence.PersistenceException;
import java.util.concurrent.ConcurrentHashMap;

final class InMemoryRepositorySupport {

    private InMemoryRepositorySupport() {}

    static <T> void putUnique(ConcurrentHashMap<String, T> storage, String id, T value, String collectionName) {
        if (storage.putIfAbsent(id, value) != null) {
            throw new PersistenceException("Duplicate runtime id in " + collectionName + ": " + id);
        }
    }

    static <T> T requirePresent(ConcurrentHashMap<String, T> storage, String id, String collectionName) {
        T value = storage.get(id);
        if (value == null) {
            throw new PersistenceException("Missing runtime id in " + collectionName + ": " + id);
        }
        return value;
    }
}
