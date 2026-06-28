package com.chugalkhorbandar.domain.cognition.ports;

import com.chugalkhorbandar.application.cognition.CognitiveAnalysisDiagnostic;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import java.util.List;
import java.util.Optional;

public interface CognitiveAnalysisRepository {

    CognitiveAnalysisResult save(CognitiveAnalysisResult result);

    Optional<CognitiveAnalysisResult> findLatestByCharacterId(String characterId);

    Optional<CognitiveAnalysisResult> findByConversationId(String characterId, String conversationId);

    List<CognitiveAnalysisResult> findAllByCharacterId(String characterId);

    CognitiveAnalysisDiagnostic saveDiagnostic(CognitiveAnalysisDiagnostic diagnostic);
}
