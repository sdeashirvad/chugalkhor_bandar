package com.chugalkhorbandar.application.world.living;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
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
class BirthdayEngineTest {

    @Mock
    private WorldRepositoryProvider worldRepositoryProvider;

    @Mock
    private CharacterRepository characterRepository;

    private BirthdayEngine engine;

    @BeforeEach
    void setUp() {
        LivingWorldProperties properties = new LivingWorldProperties();
        properties.setBirthdayEnabled(true);
        when(worldRepositoryProvider.characters()).thenReturn(characterRepository);
        engine = new BirthdayEngine(properties, worldRepositoryProvider);
    }

    @Test
    void generatesBirthdayEventWhenCharacterBirthdayMatchesToday() {
        LocalDate today = LocalDate.parse("2026-06-27");
        when(characterRepository.findAll(CharacterQuery.all()))
                .thenReturn(List.of(new RuntimeCharacter(
                        "character_alpha",
                        "Alpha",
                        Map.of(),
                        null,
                        Map.of("birthday", "06-27"))));

        LivingWorldGeneratorResult result = engine.generate(context(today));

        assertThat(result.events()).hasSize(1);
        assertThat(result.events().get(0).type()).isEqualTo(WorldEventType.BIRTHDAY);
        assertThat(result.events().get(0).participants()).containsExactly("character_alpha");
    }

    @Test
    void skipsWhenNoBirthdayToday() {
        when(characterRepository.findAll(CharacterQuery.all()))
                .thenReturn(List.of(new RuntimeCharacter(
                        "character_alpha",
                        "Alpha",
                        Map.of(),
                        null,
                        Map.of("birthday", "01-01"))));

        LivingWorldGeneratorResult result = engine.generate(context(LocalDate.parse("2026-06-27")));

        assertThat(result.events()).isEmpty();
        assertThat(result.trace()).anyMatch(entry -> entry.rule().equals("no-birthday"));
    }

    private static LivingWorldContext context(LocalDate today) {
        Instant now = today.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
        return new LivingWorldContext(
                now, today, WorldClockMode.DAILY, List.of(), List.of(), List.of(), List.of(), List.of(), Set.of());
    }
}
