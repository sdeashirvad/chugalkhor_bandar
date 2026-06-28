package com.chugalkhorbandar.application.world.living;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryLivingWorldStore;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldTickHistoryRepository;
import com.chugalkhorbandar.domain.world.living.ports.WorldTickHistoryRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorldClockTest {

    @Mock
    private LivingWorldProperties properties;

    private WorldTickHistoryRepository tickHistoryRepository;
    private WorldClock worldClock;

    @BeforeEach
    void setUp() {
        tickHistoryRepository = new InMemoryWorldTickHistoryRepository(new InMemoryLivingWorldStore());
        worldClock = new WorldClock(properties, tickHistoryRepository);
    }

    @Test
    void includesHourlyAndDailyWhenEnabled() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.isHourlyEnabled()).thenReturn(true);
        when(properties.isDailyEnabled()).thenReturn(true);

        List<WorldClockMode> modes = worldClock.scheduledModesDue(Instant.parse("2026-06-01T12:00:00Z"));

        assertThat(modes).contains(WorldClockMode.HOURLY, WorldClockMode.DAILY);
    }

    @Test
    void skipsDailyWhenAlreadyTickedToday() {
        when(properties.isEnabled()).thenReturn(true);
        when(properties.isHourlyEnabled()).thenReturn(true);
        when(properties.isDailyEnabled()).thenReturn(true);
        tickHistoryRepository.save(new WorldTickHistory(
                "run-1",
                WorldClockMode.DAILY,
                Instant.parse("2026-06-01T06:00:00Z"),
                Instant.parse("2026-06-01T06:00:01Z"),
                10,
                LocalDate.parse("2026-06-01"),
                0,
                0,
                0,
                List.of(),
                List.of(),
                List.of(),
                List.of()));

        List<WorldClockMode> modes = worldClock.scheduledModesDue(Instant.parse("2026-06-01T12:00:00Z"));

        assertThat(modes).contains(WorldClockMode.HOURLY);
        assertThat(modes).doesNotContain(WorldClockMode.DAILY);
    }
}
