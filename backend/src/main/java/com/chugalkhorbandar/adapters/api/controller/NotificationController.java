package com.chugalkhorbandar.adapters.api.controller;

import com.chugalkhorbandar.adapters.api.SessionRequestSupport;
import com.chugalkhorbandar.adapters.api.dto.NotificationGenerationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.NotificationResponseDto;
import com.chugalkhorbandar.adapters.api.dto.NotificationUnreadCountResponseDto;
import com.chugalkhorbandar.adapters.api.mapper.NotificationDtoMapper;
import com.chugalkhorbandar.application.notification.NotificationNotFoundException;
import com.chugalkhorbandar.application.notification.NotificationService;
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
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Living notification center")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "List current user's active notifications")
    public List<NotificationResponseDto> list(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return notificationService.listForSession(sessionId).stream()
                .map(NotificationDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Unread notification count for current user")
    public NotificationUnreadCountResponseDto unreadCount(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return new NotificationUnreadCountResponseDto(notificationService.unreadCountForSession(sessionId));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public NotificationResponseDto markRead(@PathVariable String id, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return NotificationDtoMapper.toDto(notificationService.markRead(sessionId, id));
    }

    @PostMapping("/{id}/dismiss")
    @Operation(summary = "Dismiss a notification")
    public NotificationResponseDto dismiss(@PathVariable String id, HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return NotificationDtoMapper.toDto(notificationService.dismiss(sessionId, id));
    }

    @GetMapping("/dev/generation")
    @Operation(summary = "Return the latest notification generation trace (developer)")
    public NotificationGenerationResponseDto latestGeneration(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return NotificationDtoMapper.toDto(notificationService
                .getLatestGeneration(sessionId)
                .orElseThrow(NotificationNotFoundException::new));
    }

    @GetMapping("/dev/all")
    @Operation(summary = "Return all notifications for current character (developer)")
    public List<NotificationResponseDto> listAll(HttpServletRequest request) {
        String sessionId = SessionRequestSupport.resolveSessionId(request);
        return notificationService.listAllForDeveloper(sessionId).stream()
                .map(NotificationDtoMapper::toDto)
                .toList();
    }
}
