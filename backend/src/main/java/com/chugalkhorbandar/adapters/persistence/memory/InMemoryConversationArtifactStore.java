package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryConversationArtifactStore {

    private final ConcurrentHashMap<String, ConversationArtifact> artifactsById = new ConcurrentHashMap<>();

    public List<ConversationArtifact> findRelevantForCharacter(String characterId) {
        return artifactsById.values().stream()
                .filter(artifact -> characterId.equals(artifact.ownerCharacterId())
                        || characterId.equals(artifact.recipientCharacterId()))
                .sorted(Comparator.comparing(ConversationArtifact::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<ConversationArtifact> findAllForCharacter(String characterId) {
        return artifactsById.values().stream()
                .filter(artifact -> characterId.equals(artifact.ownerCharacterId())
                        || characterId.equals(artifact.recipientCharacterId())
                        || characterId.equals(artifact.createdByCharacterId()))
                .sorted(Comparator.comparing(ConversationArtifact::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<ConversationArtifact> findById(String id) {
        return Optional.ofNullable(artifactsById.get(id));
    }

    public ConversationArtifact save(ConversationArtifact artifact) {
        artifactsById.put(artifact.id(), artifact);
        return artifact;
    }
}
