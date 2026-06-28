package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.ConversationArtifactGenerationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ConversationArtifactResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.ConversationArtifactDtoMapper;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactNotFoundException;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/artifacts")
@Tag(name = "Conversation Artifacts", description = "Unfinished intentions from conversations")
public class ConversationArtifactController {

    private final ConversationArtifactService artifactService;

    public ConversationArtifactController(ConversationArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @GetMapping
    @Operation(summary = "List artifacts relevant to the current character")
    public List<ConversationArtifactResponseDto> list(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return artifactService.listForSession(sessionId).stream()
                .map(ConversationArtifactDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get artifact details")
    public ConversationArtifactResponseDto get(@PathVariable String id, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ConversationArtifactDtoMapper.toDto(artifactService.getForSession(sessionId, id));
    }

    @PostMapping("/{id}/fulfill")
    @Operation(summary = "Mark an artifact as fulfilled")
    public ConversationArtifactResponseDto fulfill(@PathVariable String id, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ConversationArtifactDtoMapper.toDto(artifactService.fulfill(sessionId, id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an artifact")
    public ConversationArtifactResponseDto cancel(@PathVariable String id, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ConversationArtifactDtoMapper.toDto(artifactService.cancel(sessionId, id));
    }

    @GetMapping("/dev/generation")
    @Operation(summary = "Return the latest artifact generation trace (developer)")
    public ConversationArtifactGenerationResponseDto latestGeneration(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ConversationArtifactDtoMapper.toDto(artifactService
                .getLatestGeneration(sessionId)
                .orElseThrow(ConversationArtifactNotFoundException::new));
    }

    @GetMapping("/dev/all")
    @Operation(summary = "Return all artifacts for current character (developer)")
    public List<ConversationArtifactResponseDto> listAll(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return artifactService.listAllForDeveloper(sessionId).stream()
                .map(ConversationArtifactDtoMapper::toDto)
                .toList();
    }
}
