package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.notification.Notification;
import com.chugalkhorbandar.application.notification.NotificationPriority;
import com.chugalkhorbandar.application.notification.NotificationProperties;
import com.chugalkhorbandar.application.notification.NotificationStatus;
import com.chugalkhorbandar.application.notification.NotificationType;
import com.chugalkhorbandar.domain.notification.ports.NotificationRepository;
import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LivingWorldNotificationBridge {

    private final NotificationProperties notificationProperties;
    private final NotificationRepository notificationRepository;
    private final WorldRepositoryProvider worldRepositoryProvider;

    public LivingWorldNotificationBridge(
            NotificationProperties notificationProperties,
            NotificationRepository notificationRepository,
            WorldRepositoryProvider worldRepositoryProvider) {
        this.notificationProperties = notificationProperties;
        this.notificationRepository = notificationRepository;
        this.worldRepositoryProvider = worldRepositoryProvider;
    }

    public List<Notification> deliverFromWorldTick(
            List<WorldEvent> events, List<ConversationArtifact> artifacts, Instant now) {
        List<Notification> created = new ArrayList<>();
        for (WorldEvent event : events) {
            Notification notification = notificationForEvent(event, now);
            if (notification != null) {
                created.add(notificationRepository.save(notification.withStatus(NotificationStatus.PENDING)));
            }
        }
        for (ConversationArtifact artifact : artifacts) {
            if (artifact.type() == ConversationArtifactType.GOSSIP) {
                Notification notification = buildNotification(
                        artifact.recipientCharacterId(),
                        NotificationType.GOSSIP,
                        NotificationPriority.MEDIUM,
                        artifact.title(),
                        artifact.summary(),
                        "gossip-artifact",
                        now,
                        Map.of("artifactId", artifact.id()));
                created.add(notificationRepository.save(notification.withStatus(NotificationStatus.PENDING)));
            }
        }
        return created;
    }

    private Notification notificationForEvent(WorldEvent event, Instant now) {
        return switch (event.type()) {
            case FESTIVAL -> buildForParticipants(
                    event,
                    NotificationType.FESTIVAL,
                    NotificationPriority.HIGH,
                    "world-festival",
                    now);
            case BIRTHDAY -> buildForParticipants(
                    event,
                    NotificationType.BIRTHDAY,
                    NotificationPriority.HIGH,
                    "world-birthday",
                    now);
            case PROMISE_DUE -> buildForParticipants(
                    event,
                    NotificationType.REMINDER,
                    NotificationPriority.HIGH,
                    "promise-due",
                    now);
            case CHARACTER_ACTIVITY -> buildForParticipants(
                    event,
                    NotificationType.WORLD_EVENT,
                    NotificationPriority.MEDIUM,
                    "character-initiative",
                    now);
            case ANNOUNCEMENT -> buildForParticipants(
                    event,
                    NotificationType.GOSSIP,
                    NotificationPriority.LOW,
                    "world-gossip",
                    now);
            default -> null;
        };
    }

    private Notification buildForParticipants(
            WorldEvent event,
            NotificationType type,
            NotificationPriority priority,
            String trigger,
            Instant now) {
        if (event.participants().isEmpty()) {
            return null;
        }
        String recipient = event.participants().getLast();
        return buildNotification(recipient, type, priority, event.title(), event.summary(), trigger, now, Map.of(
                "worldEventId", event.id(),
                "worldEventType", event.type().name()));
    }

    private Notification buildNotification(
            String recipientCharacterId,
            NotificationType type,
            NotificationPriority priority,
            String title,
            String summary,
            String trigger,
            Instant now,
            Map<String, String> metadata) {
        CharacterRepository characters = worldRepositoryProvider.characters();
        String displayName = characters.findById(recipientCharacterId)
                .map(RuntimeCharacter::title)
                .orElse(recipientCharacterId);
        Map<String, String> enriched = new java.util.LinkedHashMap<>(metadata);
        enriched.put("characterName", displayName);
        Instant expiresAt = now.plus(notificationProperties.getNotificationExpirationDays(), ChronoUnit.DAYS);
        return new Notification(
                UUID.randomUUID().toString(),
                recipientCharacterId,
                type,
                priority,
                title,
                summary,
                NotificationStatus.PENDING,
                now,
                expiresAt,
                "living-world-engine",
                trigger,
                enriched);
    }
}
