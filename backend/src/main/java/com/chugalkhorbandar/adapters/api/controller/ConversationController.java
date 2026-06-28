package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.AppendMessagesResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ConversationMessagesResponseDto;
import com.chugalkhorbandar.adapters.api.dto.ConversationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.SendMessageRequestDto;
import com.chugalkhorbandar.adapters.api.mapper.ConversationDtoMapper;
import com.chugalkhorbandar.application.conversation.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversations", description = "Conversation engine")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start a conversation for the current session")
    public ConversationResponseDto create(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ConversationDtoMapper.toDto(conversationService.startConversation(sessionId));
    }

    @GetMapping("/current")
    @Operation(summary = "Get the current active conversation")
    public ConversationResponseDto current(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return ConversationDtoMapper.toDto(conversationService.currentConversation(sessionId));
    }

    @PostMapping("/current/messages")
    @Operation(summary = "Send a user message and receive Bandar echo")
    public AppendMessagesResponseDto sendMessage(
            HttpServletRequest request, @RequestBody SendMessageRequestDto body) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return new AppendMessagesResponseDto(ConversationDtoMapper.toDtos(
                conversationService.appendUserMessage(sessionId, body.content())));
    }

    @GetMapping("/current/messages")
    @Operation(summary = "List all messages in the current conversation")
    public ConversationMessagesResponseDto messages(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return new ConversationMessagesResponseDto(
                ConversationDtoMapper.toDtos(conversationService.getMessages(sessionId)));
    }
}
