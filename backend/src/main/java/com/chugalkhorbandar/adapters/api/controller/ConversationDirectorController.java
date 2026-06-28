package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.ConversationPlanResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.ConversationDirectorDtoMapper;
import com.chugalkhorbandar.application.conversation.director.ConversationDirectorService;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversation/director")
@Tag(name = "Conversation Director", description = "Deterministic conversation planning (developer)")
public class ConversationDirectorController {

    private final ConversationDirectorService conversationDirectorService;

    public ConversationDirectorController(ConversationDirectorService conversationDirectorService) {
        this.conversationDirectorService = conversationDirectorService;
    }

    @GetMapping("/current-plan")
    @Operation(summary = "Return the most recent ConversationPlan for the active session")
    public ConversationPlanResponseDto currentPlan(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ConversationDirectorDtoMapper.toDto(conversationDirectorService
                .getCurrentPlan(sessionId)
                .orElseThrow(ConversationPlanNotFoundException::new));
    }
}
