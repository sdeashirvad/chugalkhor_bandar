package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BirthdayEngine {

    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern("MM-dd");

    private final LivingWorldProperties properties;
    private final WorldRepositoryProvider worldRepositoryProvider;

    public BirthdayEngine(LivingWorldProperties properties, WorldRepositoryProvider worldRepositoryProvider) {
        this.properties = properties;
        this.worldRepositoryProvider = worldRepositoryProvider;
    }

    public LivingWorldGeneratorResult generate(LivingWorldContext context) {
        if (!properties.isBirthdayEnabled()) {
            return LivingWorldGeneratorResult.empty();
        }
        List<WorldEvent> events = new ArrayList<>();
        List<LivingWorldTraceEntry> trace = new ArrayList<>();
        CharacterRepository characters = worldRepositoryProvider.characters();
        for (RuntimeCharacter character : characters.findAll(CharacterQuery.all())) {
            if (!isBirthdayToday(character, context.today())) {
                continue;
            }
            String eventId = WorldEventIdFactory.create(WorldEventType.BIRTHDAY, context.today(), character.id());
            if (context.existingEventIds().contains(eventId)) {
                trace.add(new LivingWorldTraceEntry("BirthdayEngine", "skip-duplicate", eventId));
                continue;
            }
            events.add(new WorldEvent(
                    eventId,
                    WorldEventType.BIRTHDAY,
                    character.title() + "'s Birthday",
                    "Today is " + character.title() + "'s birthday in the Jungle.",
                    List.of(character.id()),
                    WorldEventVisibility.PUBLIC,
                    context.now(),
                    context.today(),
                    java.util.Map.of("characterId", character.id()),
                    WorldEventStatus.ACTIVE,
                    WorldEventOrigin.BIRTHDAY_ENGINE));
            trace.add(new LivingWorldTraceEntry("BirthdayEngine", "birthday-today", character.title()));
        }
        if (events.isEmpty()) {
            trace.add(new LivingWorldTraceEntry("BirthdayEngine", "no-birthday", "No character birthday today"));
        }
        return new LivingWorldGeneratorResult(events, trace);
    }

    private static boolean isBirthdayToday(RuntimeCharacter character, LocalDate today) {
        String birthday = character.preferences().get("birthday");
        if (birthday == null || birthday.isBlank()) {
            birthday = character.sections().get("birthday");
        }
        if (birthday == null || birthday.isBlank()) {
            return false;
        }
        try {
            LocalDate parsed =
                    LocalDate.parse("2024-" + normalizeMonthDay(birthday), DateTimeFormatter.ISO_LOCAL_DATE);
            return parsed.getMonthValue() == today.getMonthValue() && parsed.getDayOfMonth() == today.getDayOfMonth();
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    private static String normalizeMonthDay(String value) {
        String trimmed = value.trim();
        if (trimmed.length() == 5 && trimmed.charAt(2) == '-') {
            return trimmed;
        }
        return LocalDate.parse(trimmed, MONTH_DAY).format(DateTimeFormatter.ofPattern("MM-dd"));
    }
}
