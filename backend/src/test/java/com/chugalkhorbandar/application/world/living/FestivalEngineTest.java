package com.chugalkhorbandar.application.world.living;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.domain.world.ports.CustomRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FestivalEngineTest {

    @Mock
    private WorldRepositoryProvider worldRepositoryProvider;

    @Mock
    private CustomRepository customRepository;

    private FestivalEngine engine;

    @BeforeEach
    void setUp() {
        LivingWorldProperties properties = new LivingWorldProperties();
        properties.setFestivalEnabled(true);
        when(worldRepositoryProvider.customs()).thenReturn(customRepository);
        engine = new FestivalEngine(properties, worldRepositoryProvider);
    }

    @Test
    void generatesFestivalEventWhenDateMatchesToday() {
        LocalDate today = LocalDate.parse("2026-06-27");
        when(customRepository.findAll()).thenReturn(List.of(new RuntimeCustom(
                "custom_spring_festival",
                "Spring Festival",
                Map.of("festivalDate", "06-27"))));

        LivingWorldGeneratorResult result = engine.generate(context(today));

        assertThat(result.events()).hasSize(1);
        assertThat(result.events().get(0).type()).isEqualTo(WorldEventType.FESTIVAL);
        assertThat(result.events().get(0).title()).isEqualTo("Spring Festival");
    }

    @Test
    void skipsWhenNoFestivalToday() {
        when(customRepository.findAll()).thenReturn(List.of(new RuntimeCustom(
                "custom_spring_festival",
                "Spring Festival",
                Map.of("festivalDate", "12-25"))));

        LivingWorldGeneratorResult result = engine.generate(context(LocalDate.parse("2026-06-27")));

        assertThat(result.events()).isEmpty();
        assertThat(result.trace()).anyMatch(entry -> entry.rule().equals("no-festival"));
    }

    private static LivingWorldContext context(LocalDate today) {
        Instant now = today.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
        return new LivingWorldContext(
                now, today, WorldClockMode.DAILY, List.of(), List.of(), List.of(), List.of(), List.of(), Set.of());
    }
}
