package com.chugalkhorbandar.application.world.living;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryLivingWorldStore;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldEventRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldTickHistoryRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LivingWorldPersistenceTest {

    private InMemoryLivingWorldStore store;
    private InMemoryWorldEventRepository eventRepository;
    private InMemoryWorldTickHistoryRepository tickHistoryRepository;

    @BeforeEach
    void setUp() {
        store = new InMemoryLivingWorldStore();
        eventRepository = new InMemoryWorldEventRepository(store);
        tickHistoryRepository = new InMemoryWorldTickHistoryRepository(store);
    }

    @Test
    void persistsEventsAndTickHistory() {
        Instant now = Instant.parse("2026-06-27T12:00:00Z");
        WorldEvent event = new WorldEvent(
                "evt-birthday-2026-06-27-character_alpha",
                WorldEventType.BIRTHDAY,
                "Alpha's Birthday",
                "Birthday today",
                List.of("character_alpha"),
                WorldEventVisibility.PUBLIC,
                now,
                LocalDate.parse("2026-06-27"),
                java.util.Map.of(),
                WorldEventStatus.ACTIVE,
                WorldEventOrigin.BIRTHDAY_ENGINE);

        eventRepository.save(event);
        tickHistoryRepository.save(new WorldTickHistory(
                "run-1",
                WorldClockMode.MANUAL,
                now,
                now.plusMillis(5),
                5,
                LocalDate.parse("2026-06-27"),
                1,
                1,
                1,
                List.of("BirthdayEngine"),
                List.of(event.id()),
                List.of("art-1"),
                List.of("notif-1")));

        assertThat(eventRepository.findById(event.id())).isPresent();
        assertThat(eventRepository.findByTypeOrderByCreatedAtDesc(WorldEventType.BIRTHDAY)).hasSize(1);
        assertThat(tickHistoryRepository.findAllOrderByStartedAtDesc()).hasSize(1);
        assertThat(tickHistoryRepository.findLatestByMode(WorldClockMode.MANUAL)).isPresent();
    }
}
