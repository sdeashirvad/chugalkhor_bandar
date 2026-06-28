package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.CognitiveAnalysisExecutionResponseDto;
import com.chugalkhorbandar.adapters.api.dto.CognitiveAnalysisResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ObservationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.RecommendationResponseDto;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisExecutionSnapshot;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisResult;
import com.chugalkhorbandar.application.cognition.Observation;
import com.chugalkhorbandar.application.cognition.Recommendation;

public final class CognitiveAnalysisDtoMapper {

    private CognitiveAnalysisDtoMapper() {}

    public static CognitiveAnalysisResponseDto toDto(CognitiveAnalysisResult result) {
        return new CognitiveAnalysisResponseDto(
                result.analysisId(),
                result.characterId(),
                result.conversationId(),
                result.provider(),
                result.model(),
                result.latencyMs(),
                result.confidence(),
                result.createdAt(),
                result.observations().stream().map(CognitiveAnalysisDtoMapper::toDto).toList(),
                result.recommendations().stream().map(CognitiveAnalysisDtoMapper::toDto).toList(),
                result.rawJson());
    }

    public static ObservationResponseDto toDto(Observation observation) {
        return new ObservationResponseDto(
                observation.id(),
                observation.type(),
                observation.confidence(),
                observation.summary(),
                observation.evidence(),
                observation.metadata(),
                observation.createdAt());
    }

    public static RecommendationResponseDto toDto(Recommendation recommendation) {
        return new RecommendationResponseDto(
                recommendation.id(),
                recommendation.action(),
                recommendation.confidence(),
                recommendation.reason(),
                recommendation.target(),
                recommendation.metadata());
    }

    public static CognitiveAnalysisExecutionResponseDto toDto(CognitiveAnalysisExecutionSnapshot snapshot) {
        return new CognitiveAnalysisExecutionResponseDto(
                snapshot.characterId(),
                snapshot.conversationId(),
                snapshot.success(),
                snapshot.provider(),
                snapshot.model(),
                snapshot.providerLatencyMs(),
                snapshot.executionTimeMs(),
                snapshot.confidence(),
                snapshot.errorMessage(),
                snapshot.completedAt(),
                snapshot.resultOptional().map(CognitiveAnalysisDtoMapper::toDto).orElse(null));
    }
}
