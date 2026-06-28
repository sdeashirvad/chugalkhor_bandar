package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.CognitiveAnalysisExecutionResponseDto;
import com.chugalkhorbandar.adapters.api.dto.CognitiveAnalysisResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ObservationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.RecommendationResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.CognitiveAnalysisDtoMapper;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisNotFoundException;
import com.chugalkhorbandar.application.cognition.CognitiveAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cognition")
@Tag(name = "Cognitive Analysis", description = "Structured conversation analysis for developer inspection")
public class CognitiveAnalysisController {

    private final CognitiveAnalysisService cognitiveAnalysisService;

    public CognitiveAnalysisController(CognitiveAnalysisService cognitiveAnalysisService) {
        this.cognitiveAnalysisService = cognitiveAnalysisService;
    }

    @GetMapping("/latest")
    @Operation(summary = "Latest cognitive analysis for current character")
    public CognitiveAnalysisResponseDto latest(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return CognitiveAnalysisDtoMapper.toDto(cognitiveAnalysisService
                .getLatestForSession(sessionId)
                .orElseThrow(CognitiveAnalysisNotFoundException::new));
    }

    @GetMapping("/{conversationId}")
    @Operation(summary = "Cognitive analysis for a conversation")
    public CognitiveAnalysisResponseDto forConversation(
            @PathVariable String conversationId, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return CognitiveAnalysisDtoMapper.toDto(cognitiveAnalysisService
                .getForConversation(sessionId, conversationId)
                .orElseThrow(CognitiveAnalysisNotFoundException::new));
    }

    @GetMapping("/observations")
    @Operation(summary = "List observations from stored analyses")
    public List<ObservationResponseDto> observations(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return cognitiveAnalysisService.listObservationsForSession(sessionId).stream()
                .map(CognitiveAnalysisDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/recommendations")
    @Operation(summary = "List recommendations from stored analyses")
    public List<RecommendationResponseDto> recommendations(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return cognitiveAnalysisService.listRecommendationsForSession(sessionId).stream()
                .map(CognitiveAnalysisDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/dev/execution")
    @Operation(summary = "Latest analysis execution snapshot (developer)")
    public CognitiveAnalysisExecutionResponseDto latestExecution(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return CognitiveAnalysisDtoMapper.toDto(cognitiveAnalysisService
                .getLatestExecution(sessionId)
                .orElseThrow(CognitiveAnalysisNotFoundException::new));
    }

    @GetMapping("/dev/all")
    @Operation(summary = "All stored analyses for current character (developer)")
    public List<CognitiveAnalysisResponseDto> listAll(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return cognitiveAnalysisService.listAllForDeveloper(sessionId).stream()
                .map(CognitiveAnalysisDtoMapper::toDto)
                .toList();
    }
}
