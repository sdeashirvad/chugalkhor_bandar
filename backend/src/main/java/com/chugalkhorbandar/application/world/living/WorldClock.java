package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.domain.world.living.ports.WorldTickHistoryRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class WorldClock {

    private final LivingWorldProperties properties;
    private final WorldTickHistoryRepository tickHistoryRepository;

    public WorldClock(LivingWorldProperties properties, WorldTickHistoryRepository tickHistoryRepository) {
        this.properties = properties;
        this.tickHistoryRepository = tickHistoryRepository;
    }

    public List<WorldClockMode> scheduledModesDue(Instant now) {
        if (!properties.isEnabled()) {
            return List.of();
        }
        LocalDate today = LocalDate.ofInstant(now, ZoneId.systemDefault());
        List<WorldClockMode> due = new ArrayList<>();
        if (properties.isHourlyEnabled()) {
            due.add(WorldClockMode.HOURLY);
        }
        if (properties.isDailyEnabled() && !alreadyTicked(WorldClockMode.DAILY, today)) {
            due.add(WorldClockMode.DAILY);
        }
        if (properties.isDailyEnabled()
                && today.getDayOfWeek() == DayOfWeek.SUNDAY
                && !alreadyTicked(WorldClockMode.WEEKLY, today)) {
            due.add(WorldClockMode.WEEKLY);
        }
        return due;
    }

    public LocalDate currentWorldDate(Instant now) {
        return LocalDate.ofInstant(now, ZoneId.systemDefault());
    }

    private boolean alreadyTicked(WorldClockMode mode, LocalDate today) {
        Optional<WorldTickHistory> latest = tickHistoryRepository.findLatestByMode(mode);
        if (latest.isEmpty()) {
            return false;
        }
        LocalDate lastDate = latest.get().worldDate();
        return switch (mode) {
            case DAILY -> lastDate.equals(today);
            case WEEKLY -> !lastDate.isBefore(today.minusDays(6));
            default -> false;
        };
    }
}
