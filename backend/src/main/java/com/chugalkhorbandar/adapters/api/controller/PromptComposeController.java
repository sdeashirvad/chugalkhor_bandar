package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.ComposedPromptResponseDto;
import com.chugalkhorbandar.adapters.api.dto.PromptComposeRequestDto;
import com.chugalkhorbandar.adapters.api.mapper.PromptComposeDtoMapper;
import com.chugalkhorbandar.application.llm.PromptToProviderAdapter;
import com.chugalkhorbandar.application.prompt.PromptComposeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompt")
@Tag(name = "Prompt", description = "Semantic prompt composition (developer)")
public class PromptComposeController {

    private final PromptComposeService promptComposeService;
    private final PromptToProviderAdapter promptToProviderAdapter;

    public PromptComposeController(
            PromptComposeService promptComposeService, PromptToProviderAdapter promptToProviderAdapter) {
        this.promptComposeService = promptComposeService;
        this.promptToProviderAdapter = promptToProviderAdapter;
    }

    @PostMapping("/compose")
    @Operation(summary = "Compose a provider-independent prompt from resolved context")
    public ComposedPromptResponseDto compose(HttpServletRequest request, @RequestBody PromptComposeRequestDto body) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return PromptComposeDtoMapper.toDto(
                promptComposeService.compose(sessionId, body.latestMessage()), promptToProviderAdapter);
    }
}
