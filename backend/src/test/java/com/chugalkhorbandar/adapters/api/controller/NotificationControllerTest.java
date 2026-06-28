package com.chugalkhorbandar.adapters.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chugalkhorbandar.adapters.api.ApiExceptionHandler;
import com.chugalkhorbandar.application.notification.Notification;
import com.chugalkhorbandar.application.notification.NotificationPriority;
import com.chugalkhorbandar.application.notification.NotificationService;
import com.chugalkhorbandar.application.notification.NotificationStatus;
import com.chugalkhorbandar.application.notification.NotificationType;
import com.chugalkhorbandar.application.session.SessionConstants;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = NotificationController.class)
@Import(ApiExceptionHandler.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void listReturnsNotifications() throws Exception {
        when(notificationService.listForSession(eq("session-1")))
                .thenReturn(List.of(new Notification(
                        "n-1",
                        "character_alpha",
                        NotificationType.GREETING,
                        NotificationPriority.MEDIUM,
                        "Alpha...",
                        "before we begin today...",
                        NotificationStatus.DELIVERED,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Instant.parse("2026-02-01T00:00:00Z"),
                        "living-notification-engine",
                        "daily-greeting",
                        Map.of())));

        mockMvc.perform(get("/api/notifications").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("GREETING"))
                .andExpect(jsonPath("$[0].status").value("DELIVERED"));
    }

    @Test
    void unreadCountReturnsCount() throws Exception {
        when(notificationService.unreadCountForSession(eq("session-1"))).thenReturn(2L);

        mockMvc.perform(get("/api/notifications/unread-count").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(2));
    }

    @Test
    void markReadReturnsUpdatedNotification() throws Exception {
        when(notificationService.markRead(eq("session-1"), eq("n-1")))
                .thenReturn(new Notification(
                        "n-1",
                        "character_alpha",
                        NotificationType.GREETING,
                        NotificationPriority.MEDIUM,
                        "Alpha...",
                        "Summary",
                        NotificationStatus.READ,
                        Instant.parse("2026-01-01T00:00:00Z"),
                        Instant.parse("2026-02-01T00:00:00Z"),
                        "living-notification-engine",
                        "daily-greeting",
                        Map.of()));

        mockMvc.perform(post("/api/notifications/n-1/read").header(SessionConstants.SESSION_HEADER, "session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READ"));
    }
}
