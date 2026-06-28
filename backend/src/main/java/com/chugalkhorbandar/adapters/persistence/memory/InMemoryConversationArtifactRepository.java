package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.domain.artifacts.ports.ConversationArtifactRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryConversationArtifactRepository implements ConversationArtifactRepository {

    private final InMemoryConversationArtifactStore store;

    public InMemoryConversationArtifactRepository(InMemoryConversationArtifactStore store) {
        this.store = store;
    }

    @Override
    public List<ConversationArtifact> findRelevantForCharacter(String characterId) {
        return store.findRelevantForCharacter(characterId);
    }

    @Override
    public List<ConversationArtifact> findAllForCharacter(String characterId) {
        return store.findAllForCharacter(characterId);
    }

    @Override
    public Optional<ConversationArtifact> findById(String id) {
        return store.findById(id);
    }

    @Override
    public ConversationArtifact save(ConversationArtifact artifact) {
        return store.save(artifact);
    }
}
