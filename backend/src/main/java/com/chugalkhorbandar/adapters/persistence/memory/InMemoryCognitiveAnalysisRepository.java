package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.cognition.CognitiveAnalysisDiagnostic;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.domain.cognition.ports.CognitiveAnalysisRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryCognitiveAnalysisRepository implements CognitiveAnalysisRepository {

    private final InMemoryCognitiveAnalysisStore store;

    public InMemoryCognitiveAnalysisRepository(InMemoryCognitiveAnalysisStore store) {
        this.store = store;
    }

    @Override
    public CognitiveAnalysisResult save(CognitiveAnalysisResult result) {
        return store.save(result);
    }

    @Override
    public Optional<CognitiveAnalysisResult> findLatestByCharacterId(String characterId) {
        return store.findLatestByCharacterId(characterId);
    }

    @Override
    public Optional<CognitiveAnalysisResult> findByConversationId(String characterId, String conversationId) {
        return store.findByConversationId(characterId, conversationId);
    }

    @Override
    public List<CognitiveAnalysisResult> findAllByCharacterId(String characterId) {
        return store.findAllByCharacterId(characterId);
    }

    @Override
    public CognitiveAnalysisDiagnostic saveDiagnostic(CognitiveAnalysisDiagnostic diagnostic) {
        return store.saveDiagnostic(diagnostic);
    }
}
