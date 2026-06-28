package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.MemoryInboxGenerationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.MemoryInboxItemResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.MemoryInboxDtoMapper;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxNotFoundException;
import com.chugalkhorbandar.application.memory.inbox.MemoryInboxService;
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
@RequestMapping("/api/memory/inbox")
@Tag(name = "Memory Inbox", description = "Decision queue for potential long-term memories")
public class MemoryInboxController {

    private final MemoryInboxService memoryInboxService;

    public MemoryInboxController(MemoryInboxService memoryInboxService) {
        this.memoryInboxService = memoryInboxService;
    }

    @GetMapping
    @Operation(summary = "List current memory inbox items")
    public List<MemoryInboxItemResponseDto> list(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return memoryInboxService.listForSession(sessionId).stream()
                .map(MemoryInboxDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get memory inbox item details")
    public MemoryInboxItemResponseDto get(@PathVariable String id, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return MemoryInboxDtoMapper.toDto(memoryInboxService.getForSession(sessionId, id));
    }

    @PostMapping("/{id}/review")
    @Operation(summary = "Mark inbox item as reviewed")
    public MemoryInboxItemResponseDto review(@PathVariable String id, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return MemoryInboxDtoMapper.toDto(memoryInboxService.review(sessionId, id));
    }

    @PostMapping("/{id}/discard")
    @Operation(summary = "Discard inbox item")
    public MemoryInboxItemResponseDto discard(@PathVariable String id, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return MemoryInboxDtoMapper.toDto(memoryInboxService.discard(sessionId, id));
    }

    @GetMapping("/dev/all")
    @Operation(summary = "Return all inbox items for current character (developer)")
    public List<MemoryInboxItemResponseDto> listAll(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return memoryInboxService.listAllForDeveloper(sessionId).stream()
                .map(MemoryInboxDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/dev/generation")
    @Operation(summary = "Return latest inbox generation trace (developer)")
    public MemoryInboxGenerationResponseDto latestGeneration(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return MemoryInboxDtoMapper.toDto(memoryInboxService
                .getLatestGeneration(sessionId)
                .orElseThrow(MemoryInboxNotFoundException::new));
    }
}
