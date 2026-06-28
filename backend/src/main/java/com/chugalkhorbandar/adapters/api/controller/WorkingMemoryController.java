package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.WorkingMemoryResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.WorkingMemoryDtoMapper;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/memory/working")
@Tag(name = "Working Memory", description = "Session-scoped working memory (developer)")
public class WorkingMemoryController {

    private final WorkingMemoryService workingMemoryService;

    public WorkingMemoryController(WorkingMemoryService workingMemoryService) {
        this.workingMemoryService = workingMemoryService;
    }

    @GetMapping
    @Operation(summary = "Return the current working memory for the active session")
    public WorkingMemoryResponseDto get(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return WorkingMemoryDtoMapper.toDto(workingMemoryService.getOrBuild(sessionId));
    }

    @PostMapping("/rebuild")
    @Operation(summary = "Rebuild working memory deterministically from the conversation")
    public WorkingMemoryResponseDto rebuild(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return WorkingMemoryDtoMapper.toDto(workingMemoryService.rebuild(sessionId));
    }
}
