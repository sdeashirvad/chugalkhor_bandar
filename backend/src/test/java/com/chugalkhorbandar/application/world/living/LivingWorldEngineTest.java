package com.chugalkhorbandar.application.world.living;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactPriority;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactStatus;
import com.chugalkhorbandar.application.artifacts.ConversationArtifactType;
import com.chugalkhorbandar.application.notification.Notification;
import com.chugalkhorbandar.application.notification.NotificationPriority;
import com.chugalkhorbandar.application.notification.NotificationStatus;
import com.chugalkhorbandar.application.notification.NotificationType;
import com.chugalkhorbandar.domain.world.living.ports.WorldEventRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LivingWorldEngineTest {

    @Mock
    private FestivalEngine festivalEngine;

    @Mock
    private BirthdayEngine birthdayEngine;

    @Mock
    private PromiseFulfillmentEngine promiseFulfillmentEngine;

    @Mock
    private CharacterInitiativeEngine characterInitiativeEngine;

    @Mock
    private GossipEngine gossipEngine;

    @Mock
    private LivingWorldArtifactFactory artifactFactory;

    @Mock
    private LivingWorldNotificationBridge notificationBridge;

    @Mock
    private WorldEventRepository worldEventRepository;

    private LivingWorldEngine engine;

    @BeforeEach
    void setUp() {
        LivingWorldProperties properties = new LivingWorldProperties();
        properties.setEnabled(true);
        engine = new LivingWorldEngine(
                properties,
                festivalEngine,
                birthdayEngine,
                promiseFulfillmentEngine,
                characterInitiativeEngine,
                gossipEngine,
                artifactFactory,
                notificationBridge,
                worldEventRepository);
    }

    @Test
    void tickPersistsEventsAndCreatesArtifactsAndNotifications() {
        Instant now = Instant.parse("2026-06-27T12:00:00Z");
        LocalDate today = LocalDate.parse("2026-06-27");
        WorldEvent festival = sampleEvent("evt-festival", WorldEventType.FESTIVAL);
        ConversationArtifact artifact = sampleArtifact("art-1");
        Notification notification = sampleNotification("notif-1");

        when(festivalEngine.generate(any())).thenReturn(new LivingWorldGeneratorResult(List.of(festival), List.of()));
        when(birthdayEngine.generate(any())).thenReturn(LivingWorldGeneratorResult.empty());
        when(promiseFulfillmentEngine.generate(any())).thenReturn(LivingWorldGeneratorResult.empty());
        when(characterInitiativeEngine.generate(any())).thenReturn(LivingWorldGeneratorResult.empty());
        when(gossipEngine.generate(any())).thenReturn(LivingWorldGeneratorResult.empty());
        when(worldEventRepository.save(festival)).thenReturn(festival);
        when(artifactFactory.createFromEvents(List.of(festival), now)).thenReturn(List.of(artifact));
        when(notificationBridge.deliverFromWorldTick(List.of(festival), List.of(artifact), now))
                .thenReturn(List.of(notification));

        LivingWorldTickResult result = engine.tick(new LivingWorldContext(
                now, today, WorldClockMode.MANUAL, List.of(), List.of(), List.of(), List.of(), List.of(), Set.of()));

        assertThat(result.eventsGenerated()).isEqualTo(1);
        assertThat(result.artifactsGenerated()).isEqualTo(1);
        assertThat(result.notificationsGenerated()).isEqualTo(1);
        verify(worldEventRepository).save(festival);
        verify(gossipEngine).generate(any());
    }

    private static WorldEvent sampleEvent(String id, WorldEventType type) {
        Instant now = Instant.parse("2026-06-27T12:00:00Z");
        return new WorldEvent(
                id,
                type,
                "Spring Festival",
                "Celebration",
                List.of("character_alpha"),
                WorldEventVisibility.PUBLIC,
                now,
                LocalDate.parse("2026-06-27"),
                java.util.Map.of(),
                WorldEventStatus.ACTIVE,
                WorldEventOrigin.FESTIVAL_ENGINE);
    }

    private static ConversationArtifact sampleArtifact(String id) {
        Instant now = Instant.parse("2026-06-27T12:00:00Z");
        return new ConversationArtifact(
                id,
                ConversationArtifactType.INVITATION,
                "character_bandar",
                "character_alpha",
                "character_alpha",
                "",
                "Spring Festival",
                "Celebration",
                ConversationArtifactStatus.ACTIVE,
                ConversationArtifactPriority.HIGH,
                now,
                now,
                now,
                java.util.Map.of(),
                List.of());
    }

    private static Notification sampleNotification(String id) {
        Instant now = Instant.parse("2026-06-27T12:00:00Z");
        return new Notification(
                id,
                "character_alpha",
                NotificationType.FESTIVAL,
                NotificationPriority.HIGH,
                "Spring Festival",
                "Celebration",
                NotificationStatus.PENDING,
                now,
                now,
                "living-world-engine",
                "world-festival",
                java.util.Map.of());
    }
}
