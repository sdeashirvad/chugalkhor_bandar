package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.cognition.CognitiveAnalysisDiagnostic;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryCognitiveAnalysisStore {

    private final ConcurrentHashMap<String, CognitiveAnalysisResult> analysesById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CognitiveAnalysisDiagnostic> diagnosticsById = new ConcurrentHashMap<>();

    public CognitiveAnalysisResult save(CognitiveAnalysisResult result) {
        analysesById.put(result.analysisId(), result);
        return result;
    }

    public Optional<CognitiveAnalysisResult> findLatestByCharacterId(String characterId) {
        return analysesById.values().stream()
                .filter(result -> characterId.equals(result.characterId()))
                .max(Comparator.comparing(CognitiveAnalysisResult::createdAt));
    }

    public Optional<CognitiveAnalysisResult> findByConversationId(String characterId, String conversationId) {
        return analysesById.values().stream()
                .filter(result -> characterId.equals(result.characterId()))
                .filter(result -> conversationId.equals(result.conversationId()))
                .max(Comparator.comparing(CognitiveAnalysisResult::createdAt));
    }

    public List<CognitiveAnalysisResult> findAllByCharacterId(String characterId) {
        return analysesById.values().stream()
                .filter(result -> characterId.equals(result.characterId()))
                .sorted(Comparator.comparing(CognitiveAnalysisResult::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public CognitiveAnalysisDiagnostic saveDiagnostic(CognitiveAnalysisDiagnostic diagnostic) {
        diagnosticsById.put(diagnostic.id(), diagnostic);
        return diagnostic;
    }
}
