package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.PersistenceException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

final class PostgresRepositorySupport {

    private PostgresRepositorySupport() {}

    static void ensureAbsent(JpaRepository<?, String> repository, String id, String collectionName) {
        if (repository.existsById(id)) {
            throw new PersistenceException("Duplicate runtime id in " + collectionName + ": " + id);
        }
    }

    static void ensurePresent(JpaRepository<?, String> repository, String id, String collectionName) {
        if (!repository.existsById(id)) {
            throw new PersistenceException("Missing runtime id in " + collectionName + ": " + id);
        }
    }

    static <T> T requireRuntime(Optional<T> runtime, String id, String collectionName) {
        return runtime.orElseThrow(
                () -> new PersistenceException("Missing runtime id in " + collectionName + ": " + id));
    }
}
