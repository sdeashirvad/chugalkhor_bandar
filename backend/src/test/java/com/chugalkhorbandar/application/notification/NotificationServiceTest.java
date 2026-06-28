package com.chugalkhorbandar.application.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryNotificationRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryNotificationStore;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private com.chugalkhorbandar.application.session.SessionService sessionService;

    @Mock
    private LivingNotificationEngine livingNotificationEngine;

    @Mock
    private WorldStatusQueryService worldStatusQueryService;

    @Mock
    private com.chugalkhorbandar.application.memory.working.WorkingMemoryService workingMemoryService;

    private InMemoryNotificationRepository repository;
    private NotificationProperties properties;
    private NotificationGenerationStore generationStore;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNotificationRepository(new InMemoryNotificationStore());
        properties = new NotificationProperties();
        properties.setDailyGreetingProbability(1.0);
        generationStore = new NotificationGenerationStore();
        notificationService = new NotificationService(
                sessionService,
                livingNotificationEngine,
                repository,
                properties,
                workingMemoryService,
                worldStatusQueryService,
                generationStore);
    }

    @Test
    void markReadAndDismissUpdateStatus() {
        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        Notification notification = repository.save(new Notification(
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
                Map.of()));

        Notification read = notificationService.markRead("session-1", notification.id());
        assertThat(read.status()).isEqualTo(NotificationStatus.READ);

        repository.save(notification.withStatus(NotificationStatus.DELIVERED));
        Notification dismissed = notificationService.dismiss("session-1", notification.id());
        assertThat(dismissed.status()).isEqualTo(NotificationStatus.DISMISSED);
    }

    @Test
    void unreadCountTracksDeliveredNotifications() {
        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        repository.save(new Notification(
                "n-1",
                "character_alpha",
                NotificationType.GREETING,
                NotificationPriority.MEDIUM,
                "Alpha...",
                "Summary",
                NotificationStatus.DELIVERED,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                "living-notification-engine",
                "daily-greeting",
                Map.of()));
        repository.save(new Notification(
                "n-2",
                "character_alpha",
                NotificationType.STORY,
                NotificationPriority.LOW,
                "Story",
                "Summary",
                NotificationStatus.READ,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                "living-notification-engine",
                "story",
                Map.of()));

        assertThat(notificationService.unreadCountForSession("session-1")).isEqualTo(1);
    }

    @Test
    void expiredNotificationsAreMarkedExpiredOnList() {
        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        repository.save(new Notification(
                "n-expired",
                "character_alpha",
                NotificationType.GREETING,
                NotificationPriority.LOW,
                "Alpha...",
                "Summary",
                NotificationStatus.DELIVERED,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-02T00:00:00Z"),
                "living-notification-engine",
                "daily-greeting",
                Map.of()));

        List<Notification> active = notificationService.listForSession("session-1");
        assertThat(active).noneMatch(notification -> notification.id().equals("n-expired"));
        assertThat(repository.findById("n-expired")).get().extracting(Notification::status).isEqualTo(NotificationStatus.EXPIRED);
    }

    @Test
    void priorityOrderingPlacesCriticalAndHighFirst() {
        ChatSession session = session("session-1", "character_alpha");
        when(sessionService.requireSession("session-1")).thenReturn(session);
        repository.save(notification("n-low", NotificationPriority.LOW));
        repository.save(notification("n-high", NotificationPriority.HIGH));

        List<Notification> listed = notificationService.listForSession("session-1");
        assertThat(listed.get(0).priority()).isEqualTo(NotificationPriority.HIGH);
    }

    private static Notification notification(String id, NotificationPriority priority) {
        return new Notification(
                id,
                "character_alpha",
                NotificationType.GREETING,
                priority,
                "Alpha...",
                "Summary",
                NotificationStatus.DELIVERED,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                "living-notification-engine",
                "daily-greeting",
                Map.of());
    }

    private static ChatSession session(String sessionId, String characterId) {
        return new ChatSession(
                sessionId,
                new CurrentCharacter(characterId, "Alpha", List.of("Alpha"), "Rabbitu", null, null),
                Instant.now(),
                Instant.now(),
                SessionStatus.ACTIVE);
    }
}
