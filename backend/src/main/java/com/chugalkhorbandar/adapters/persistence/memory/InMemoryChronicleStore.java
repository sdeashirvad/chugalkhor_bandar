package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.chronicle.Chronicle;
import com.chugalkhorbandar.application.chronicle.ChronicleCategory;
import com.chugalkhorbandar.application.chronicle.ChronicleVisibility;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryChronicleStore {

    private final ConcurrentHashMap<String, Chronicle> chroniclesById = new ConcurrentHashMap<>();

    public Chronicle save(Chronicle chronicle) {
        chroniclesById.put(chronicle.id(), chronicle);
        return chronicle;
    }

    public Optional<Chronicle> findById(String id) {
        return Optional.ofNullable(chroniclesById.get(id));
    }

    public Optional<Chronicle> findLatestByCandidateId(String candidateId) {
        return chroniclesById.values().stream()
                .filter(chronicle -> candidateId.equals(chronicle.provenance().candidateId()))
                .max(Comparator.comparingInt(Chronicle::version));
    }

    public int countByCandidateId(String candidateId) {
        return (int) chroniclesById.values().stream()
                .filter(chronicle -> candidateId.equals(chronicle.provenance().candidateId()))
                .count();
    }

    public List<Chronicle> findAll() {
        return chroniclesById.values().stream()
                .sorted(Comparator.comparing(Chronicle::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Chronicle> findByCategory(ChronicleCategory category) {
        return chroniclesById.values().stream()
                .filter(chronicle -> chronicle.category() == category)
                .sorted(Comparator.comparing(Chronicle::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Chronicle> findByVisibility(ChronicleVisibility visibility) {
        return chroniclesById.values().stream()
                .filter(chronicle -> chronicle.visibility() == visibility)
                .sorted(Comparator.comparing(Chronicle::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
