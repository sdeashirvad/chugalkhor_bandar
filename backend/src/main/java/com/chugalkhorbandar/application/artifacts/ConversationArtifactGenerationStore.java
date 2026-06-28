package com.chugalkhorbandar.application.artifacts;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ConversationArtifactGenerationStore {

    private final ConcurrentHashMap<String, ConversationArtifactGenerationSnapshot> latestByCharacterId =
            new ConcurrentHashMap<>();

    public void save(ConversationArtifactGenerationSnapshot snapshot) {
        latestByCharacterId.put(snapshot.characterId(), snapshot);
    }

    public Optional<ConversationArtifactGenerationSnapshot> findByCharacterId(String characterId) {
        return Optional.ofNullable(latestByCharacterId.get(characterId));
    }
}
