package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.ContextPlanRequestDto;
import com.chugalkhorbandar.adapters.api.dto.ContextPlanResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ResolvedContextResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.ContextPlanDtoMapper;
import com.chugalkhorbandar.application.context.ContextPlanService;
import com.chugalkhorbandar.application.context.ContextResolveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/context")
@Tag(name = "Context", description = "Deterministic context planning and resolution (developer)")
public class ContextPlanController {

    private final ContextPlanService contextPlanService;
    private final ContextResolveService contextResolveService;

    public ContextPlanController(
            ContextPlanService contextPlanService, ContextResolveService contextResolveService) {
        this.contextPlanService = contextPlanService;
        this.contextResolveService = contextResolveService;
    }

    @PostMapping("/plan")
    @Operation(summary = "Build a deterministic context plan for the latest user message")
    public ContextPlanResponseDto plan(HttpServletRequest request, @RequestBody ContextPlanRequestDto body) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ContextPlanDtoMapper.toDto(contextPlanService.plan(sessionId, body.latestMessage()));
    }

    @PostMapping("/resolve")
    @Operation(summary = "Resolve a context plan into actual context payloads")
    public ResolvedContextResponseDto resolve(HttpServletRequest request, @RequestBody ContextPlanRequestDto body) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ContextPlanDtoMapper.toDto(contextResolveService.resolve(sessionId, body.latestMessage()));
    }
}
