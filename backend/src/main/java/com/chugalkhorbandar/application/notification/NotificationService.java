package com.chugalkhorbandar.application.notification;

import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.SessionService;
import com.chugalkhorbandar.domain.notification.ports.NotificationRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SessionService sessionService;
    private final LivingNotificationEngine livingNotificationEngine;
    private final NotificationRepository notificationRepository;
    private final NotificationProperties properties;
    private final WorkingMemoryService workingMemoryService;
    private final WorldStatusQueryService worldStatusQueryService;
    private final NotificationGenerationStore generationStore;

    public NotificationService(
            SessionService sessionService,
            LivingNotificationEngine livingNotificationEngine,
            NotificationRepository notificationRepository,
            NotificationProperties properties,
            @Lazy WorkingMemoryService workingMemoryService,
            WorldStatusQueryService worldStatusQueryService,
            NotificationGenerationStore generationStore) {
        this.sessionService = sessionService;
        this.livingNotificationEngine = livingNotificationEngine;
        this.notificationRepository = notificationRepository;
        this.properties = properties;
        this.workingMemoryService = workingMemoryService;
        this.worldStatusQueryService = worldStatusQueryService;
        this.generationStore = generationStore;
    }

    public void generateOnLogin(ChatSession session) {
        String characterId = session.currentCharacter().id();
        Instant now = Instant.now();
        expireNotifications(characterId, now);
        List<Notification> existing = notificationRepository.findByRecipientCharacterId(characterId);
        Instant lastNotificationAt = existing.stream()
                .map(Notification::createdAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
        var worldStatus = worldStatusQueryService.getStatus();
        RuntimeWorldContext runtimeWorld = new RuntimeWorldContext(
                worldStatus.status(),
                worldStatus.bootstrapVersion(),
                worldStatus.characters(),
                worldStatus.stories(),
                List.of());
        var workingMemory = workingMemoryService
                .find(session.sessionId())
                .map(snapshot -> snapshot.memory())
                .orElse(null);
        LivingNotificationEngineInput input = new LivingNotificationEngineInput(
                session.currentCharacter(),
                runtimeWorld,
                workingMemory,
                session,
                now,
                existing,
                lastNotificationAt);
        NotificationGenerationSnapshot generation =
                livingNotificationEngine.generate(characterId, input);
        generationStore.save(generation);
        List<Notification> active = activeNotifications(existing, now);
        for (Notification notification : generation.generatedNotifications()) {
            if (active.size() >= properties.getMaximumActiveNotifications()) {
                break;
            }
            Notification delivered = notificationRepository.save(notification.withStatus(NotificationStatus.DELIVERED));
            active.add(delivered);
        }
    }

    public List<Notification> listForSession(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        String characterId = session.currentCharacter().id();
        Instant now = Instant.now();
        expireNotifications(characterId, now);
        deliverPending(characterId);
        return sortForDisplay(notificationRepository.findByRecipientCharacterId(characterId).stream()
                .filter(notification -> notification.status() != NotificationStatus.EXPIRED
                        && notification.status() != NotificationStatus.DISMISSED)
                .toList());
    }

    public long unreadCountForSession(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        expireNotifications(session.currentCharacter().id(), Instant.now());
        deliverPending(session.currentCharacter().id());
        return notificationRepository.countUnreadByRecipientCharacterId(session.currentCharacter().id());
    }

    public Notification markRead(String sessionId, String notificationId) {
        Notification notification = requireOwnedNotification(sessionId, notificationId);
        if (notification.status() == NotificationStatus.DELIVERED) {
            return notificationRepository.save(notification.withStatus(NotificationStatus.READ));
        }
        return notification;
    }

    public Notification dismiss(String sessionId, String notificationId) {
        Notification notification = requireOwnedNotification(sessionId, notificationId);
        return notificationRepository.save(notification.withStatus(NotificationStatus.DISMISSED));
    }

    public Optional<NotificationGenerationSnapshot> getLatestGeneration(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return generationStore.findByCharacterId(session.currentCharacter().id());
    }

    public List<Notification> listAllForDeveloper(String sessionId) {
        ChatSession session = sessionService.requireSession(sessionId);
        return sortForDisplay(notificationRepository.findByRecipientCharacterId(session.currentCharacter().id()));
    }

    private Notification requireOwnedNotification(String sessionId, String notificationId) {
        ChatSession session = sessionService.requireSession(sessionId);
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
        if (!session.currentCharacter().id().equals(notification.recipientCharacterId())) {
            throw new NotificationNotFoundException();
        }
        return notification;
    }

    private void deliverPending(String characterId) {
        for (Notification notification : notificationRepository.findByRecipientCharacterId(characterId)) {
            if (notification.status() == NotificationStatus.PENDING) {
                notificationRepository.save(notification.withStatus(NotificationStatus.DELIVERED));
            }
        }
    }

    private void expireNotifications(String characterId, Instant now) {
        for (Notification notification : notificationRepository.findByRecipientCharacterId(characterId)) {
            if (notification.status() != NotificationStatus.EXPIRED
                    && notification.status() != NotificationStatus.DISMISSED
                    && notification.expiresAt().isBefore(now)) {
                notificationRepository.save(notification.withStatus(NotificationStatus.EXPIRED));
            }
        }
    }

    private static List<Notification> activeNotifications(List<Notification> notifications, Instant now) {
        List<Notification> active = new ArrayList<>();
        for (Notification notification : notifications) {
            if (notification.status() != NotificationStatus.EXPIRED
                    && notification.status() != NotificationStatus.DISMISSED
                    && !notification.expiresAt().isBefore(now)) {
                active.add(notification);
            }
        }
        return active;
    }

    private static List<Notification> sortForDisplay(List<Notification> notifications) {
        return notifications.stream()
                .sorted(Comparator.comparingInt(NotificationService::priorityRank).reversed()
                        .thenComparing(Notification::createdAt, Comparator.reverseOrder()))
                .toList();
    }

    private static int priorityRank(Notification notification) {
        return switch (notification.priority()) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }
}
