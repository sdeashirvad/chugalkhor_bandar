package com.chugalkhorbandar.domain.artifacts.ports;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import java.util.List;
import java.util.Optional;

public interface ConversationArtifactRepository {

    List<ConversationArtifact> findRelevantForCharacter(String characterId);

    List<ConversationArtifact> findAllForCharacter(String characterId);

    Optional<ConversationArtifact> findById(String id);

    ConversationArtifact save(ConversationArtifact artifact);
}
