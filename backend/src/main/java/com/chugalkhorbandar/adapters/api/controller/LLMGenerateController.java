package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.LLMGenerateRequestDto;
import com.chugalkhorbandar.adapters.api.dto.LLMGenerateResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.LLMGenerateDtoMapper;
import com.chugalkhorbandar.application.llm.LLMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/llm")
@Tag(name = "LLM", description = "Provider abstraction and generation (developer)")
public class LLMGenerateController {

    private final LLMService llmService;

    public LLMGenerateController(LLMService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Run the full pipeline through the mock LLM provider")
    public LLMGenerateResponseDto generate(HttpServletRequest request, @RequestBody LLMGenerateRequestDto body) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return LLMGenerateDtoMapper.toDto(llmService.generate(sessionId, body.latestMessage()));
    }
}
