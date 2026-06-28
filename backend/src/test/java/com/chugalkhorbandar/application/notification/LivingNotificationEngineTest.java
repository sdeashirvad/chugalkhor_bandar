package com.chugalkhorbandar.application.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryNotificationStore;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryNotificationRepository;
import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.query.WorldStatus;
import com.chugalkhorbandar.application.query.WorldStatusQueryService;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LivingNotificationEngineTest {

    @Mock
    private WorldRepositoryProvider worldRepositoryProvider;

    @Mock
    private com.chugalkhorbandar.domain.world.ports.CharacterRepository characterRepository;

    @Mock
    private com.chugalkhorbandar.domain.world.ports.CustomRepository customRepository;

    private NotificationProperties properties;
    private LivingNotificationEngine engine;

    @BeforeEach
    void setUp() {
        properties = new NotificationProperties();
        properties.setDailyGreetingProbability(1.0);
        when(worldRepositoryProvider.characters()).thenReturn(characterRepository);
        when(worldRepositoryProvider.customs()).thenReturn(customRepository);
        engine = new LivingNotificationEngine(properties, worldRepositoryProvider);
    }

    @Test
    void generatesDailyGreetingDeterministically() {
        NotificationGenerationSnapshot snapshot = engine.generate(
                "character_alpha", input("Alpha", List.of(), null));

        assertThat(snapshot.generatedNotifications()).anyMatch(notification -> notification.type() == NotificationType.GREETING);
        assertThat(snapshot.trace()).extracting(NotificationGenerationTraceEntry::rule).contains("daily-greeting");
    }

    @Test
    void generatesBirthdayNotificationWhenBirthdayMatchesToday() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        when(characterRepository.findById("character_alpha"))
                .thenReturn(Optional.of(new RuntimeCharacter(
                        "character_alpha",
                        "Alpha",
                        Map.of(),
                        null,
                        Map.of("birthday", today.format(DateTimeFormatter.ofPattern("MM-dd"))))));
        NotificationGenerationSnapshot snapshot = engine.generate("character_alpha", input("Alpha", List.of(), null));

        assertThat(snapshot.generatedNotifications()).anyMatch(notification -> notification.type() == NotificationType.BIRTHDAY);
    }

    @Test
    void generatesFestivalNotificationWhenWorldContainsUpcomingFestival() {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        when(customRepository.findAll())
                .thenReturn(List.of(new RuntimeCustom(
                        "custom_festival",
                        "Moon Festival",
                        Map.of("festivalDate", today.plusDays(2).format(DateTimeFormatter.ofPattern("MM-dd"))))));
        NotificationGenerationSnapshot snapshot = engine.generate("character_alpha", input("Alpha", List.of(), null));

        assertThat(snapshot.generatedNotifications()).anyMatch(notification -> notification.type() == NotificationType.FESTIVAL);
    }

    @Test
    void producesNothingWhenNoRulesMatchAndProbabilityZero() {
        properties.setDailyGreetingProbability(0.0);
        when(characterRepository.findById("character_alpha")).thenReturn(Optional.empty());
        when(customRepository.findAll()).thenReturn(List.of());

        NotificationGenerationSnapshot snapshot = engine.generate("character_alpha", input("Alpha", List.of(), null));

        assertThat(snapshot.generatedNotifications()).isEmpty();
    }

    private static LivingNotificationEngineInput input(
            String name, List<Notification> existing, Instant lastNotificationAt) {
        return new LivingNotificationEngineInput(
                new CurrentCharacter("character_alpha", name, List.of(name), "Rabbitu", null, null),
                new RuntimeWorldContext("READY", "1.0", 1, 0, List.of()),
                null,
                new ChatSession(
                        "session-1",
                        new CurrentCharacter("character_alpha", name, List.of(name), "Rabbitu", null, null),
                        Instant.now(),
                        Instant.now(),
                        SessionStatus.ACTIVE),
                Instant.now(),
                existing,
                lastNotificationAt);
    }
}
