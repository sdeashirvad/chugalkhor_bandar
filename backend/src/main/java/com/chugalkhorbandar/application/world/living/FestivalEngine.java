package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.domain.world.ports.CustomRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FestivalEngine {

    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern("MM-dd");

    private final LivingWorldProperties properties;
    private final WorldRepositoryProvider worldRepositoryProvider;

    public FestivalEngine(LivingWorldProperties properties, WorldRepositoryProvider worldRepositoryProvider) {
        this.properties = properties;
        this.worldRepositoryProvider = worldRepositoryProvider;
    }

    public LivingWorldGeneratorResult generate(LivingWorldContext context) {
        if (!properties.isFestivalEnabled()) {
            return LivingWorldGeneratorResult.empty();
        }
        List<WorldEvent> events = new ArrayList<>();
        List<LivingWorldTraceEntry> trace = new ArrayList<>();
        CustomRepository customs = worldRepositoryProvider.customs();
        for (RuntimeCustom custom : customs.findAll()) {
            String festivalDate = custom.sections().get("festivalDate");
            if (festivalDate == null || festivalDate.isBlank()) {
                festivalDate = custom.sections().get("upcomingFestivalDate");
            }
            if (festivalDate == null || festivalDate.isBlank()) {
                continue;
            }
            Optional<LocalDate> parsed = parseMonthDay(festivalDate, context.today().getYear());
            if (parsed.isEmpty()) {
                continue;
            }
            if (!parsed.get().equals(context.today())) {
                continue;
            }
            String eventId = WorldEventIdFactory.create(WorldEventType.FESTIVAL, context.today(), custom.id());
            if (context.existingEventIds().contains(eventId)) {
                trace.add(new LivingWorldTraceEntry("FestivalEngine", "skip-duplicate", eventId));
                continue;
            }
            events.add(new WorldEvent(
                    eventId,
                    WorldEventType.FESTIVAL,
                    custom.title(),
                    "The Jungle celebrates " + custom.title() + " today.",
                    List.of(),
                    WorldEventVisibility.PUBLIC,
                    context.now(),
                    context.today(),
                    java.util.Map.of("customId", custom.id(), "festivalDate", festivalDate),
                    WorldEventStatus.ACTIVE,
                    WorldEventOrigin.FESTIVAL_ENGINE));
            trace.add(new LivingWorldTraceEntry("FestivalEngine", "festival-today", custom.title()));
        }
        if (events.isEmpty()) {
            trace.add(new LivingWorldTraceEntry("FestivalEngine", "no-festival", "No festival matches today"));
        }
        return new LivingWorldGeneratorResult(events, trace);
    }

    private static Optional<LocalDate> parseMonthDay(String value, int year) {
        try {
            String normalized = normalizeMonthDay(value);
            return Optional.of(LocalDate.parse(year + "-" + normalized, DateTimeFormatter.ISO_LOCAL_DATE));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
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
