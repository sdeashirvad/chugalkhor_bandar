package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.PromptComposeRequestDto;
import com.chugalkhorbandar.adapters.api.dto.PromptBudgetResponseDto;
import com.chugalkhorbandar.adapters.api.dto.PromptProfileResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.PromptProfileDtoMapper;
import com.chugalkhorbandar.application.prompt.budget.PromptBudgetService;
import com.chugalkhorbandar.application.prompt.profile.PromptProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prompt")
@Tag(name = "Prompt Profile & Budget", description = "Context profiles and budget allocation (developer)")
public class PromptProfileController {

    private final PromptProfileService promptProfileService;
    private final PromptBudgetService promptBudgetService;

    public PromptProfileController(
            PromptProfileService promptProfileService, PromptBudgetService promptBudgetService) {
        this.promptProfileService = promptProfileService;
        this.promptBudgetService = promptBudgetService;
    }

    @PostMapping("/profile")
    @Operation(summary = "Select a context profile for the latest user message")
    public PromptProfileResponseDto profile(HttpServletRequest request, @RequestBody PromptComposeRequestDto body) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return PromptProfileDtoMapper.toDto(promptProfileService.selectProfile(sessionId, body.latestMessage()));
    }

    @PostMapping("/budget")
    @Operation(summary = "Allocate token budgets for a composed prompt under the selected profile")
    public PromptBudgetResponseDto budget(HttpServletRequest request, @RequestBody PromptComposeRequestDto body) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return PromptProfileDtoMapper.toDto(promptBudgetService.allocate(sessionId, body.latestMessage()));
    }
}
