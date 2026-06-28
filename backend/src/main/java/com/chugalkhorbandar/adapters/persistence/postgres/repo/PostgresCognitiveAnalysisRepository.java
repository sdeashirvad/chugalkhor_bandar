package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.CognitiveAnalysisDiagnosticEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.CognitiveAnalysisEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CognitiveAnalysisDiagnosticJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CognitiveAnalysisJpaRepository;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisDiagnostic;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.application.cognition.Observation;
import com.chugalkhorbandar.application.cognition.Recommendation;
import com.chugalkhorbandar.domain.cognition.ports.CognitiveAnalysisRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresCognitiveAnalysisRepository implements CognitiveAnalysisRepository {

    private final CognitiveAnalysisJpaRepository jpaRepository;
    private final CognitiveAnalysisDiagnosticJpaRepository diagnosticJpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresCognitiveAnalysisRepository(
            CognitiveAnalysisJpaRepository jpaRepository,
            CognitiveAnalysisDiagnosticJpaRepository diagnosticJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.diagnosticJpaRepository = diagnosticJpaRepository;
    }

    @Override
    public CognitiveAnalysisResult save(CognitiveAnalysisResult result) {
        jpaRepository.save(toEntity(result));
        return result;
    }

    @Override
    public Optional<CognitiveAnalysisResult> findLatestByCharacterId(String characterId) {
        return jpaRepository.findFirstByCharacterIdOrderByCreatedAtDesc(characterId).map(this::toDomain);
    }

    @Override
    public Optional<CognitiveAnalysisResult> findByConversationId(String characterId, String conversationId) {
        return jpaRepository
                .findFirstByCharacterIdAndConversationIdOrderByCreatedAtDesc(characterId, conversationId)
                .map(this::toDomain);
    }

    @Override
    public List<CognitiveAnalysisResult> findAllByCharacterId(String characterId) {
        return jpaRepository.findByCharacterIdOrderByCreatedAtDesc(characterId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public CognitiveAnalysisDiagnostic saveDiagnostic(CognitiveAnalysisDiagnostic diagnostic) {
        diagnosticJpaRepository.save(toDiagnosticEntity(diagnostic));
        return diagnostic;
    }

    private CognitiveAnalysisResult toDomain(CognitiveAnalysisEntity entity) {
        return new CognitiveAnalysisResult(
                entity.getId(),
                entity.getCharacterId(),
                entity.getConversationId(),
                entity.getProvider(),
                entity.getModel(),
                entity.getLatencyMs(),
                entity.getConfidence(),
                entity.getCreatedAt(),
                readList(entity.getObservationsJson(), new TypeReference<List<Observation>>() {}),
                readList(entity.getRecommendationsJson(), new TypeReference<List<Recommendation>>() {}),
                entity.getRawJson());
    }

    private CognitiveAnalysisEntity toEntity(CognitiveAnalysisResult result) {
        return new CognitiveAnalysisEntity(
                result.analysisId(),
                result.characterId(),
                result.conversationId(),
                result.provider(),
                result.model(),
                result.latencyMs(),
                result.confidence(),
                result.createdAt(),
                result.rawJson(),
                writeJson(result.observations()),
                writeJson(result.recommendations()));
    }

    private CognitiveAnalysisDiagnosticEntity toDiagnosticEntity(CognitiveAnalysisDiagnostic diagnostic) {
        return new CognitiveAnalysisDiagnosticEntity(
                diagnostic.id(),
                diagnostic.characterId(),
                diagnostic.conversationId(),
                diagnostic.provider(),
                diagnostic.errorMessage(),
                diagnostic.executionTimeMs(),
                diagnostic.createdAt());
    }

    private <T> T readList(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to deserialize cognitive analysis payload", exception);
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize cognitive analysis payload", exception);
        }
    }
}
