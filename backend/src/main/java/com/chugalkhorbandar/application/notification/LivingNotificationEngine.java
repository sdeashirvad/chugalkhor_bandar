package com.chugalkhorbandar.application.notification;

import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.world.ports.CustomRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LivingNotificationEngine {

    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern("MM-dd");

    private final NotificationProperties properties;
    private final WorldRepositoryProvider worldRepositoryProvider;

    public LivingNotificationEngine(NotificationProperties properties, WorldRepositoryProvider worldRepositoryProvider) {
        this.properties = properties;
        this.worldRepositoryProvider = worldRepositoryProvider;
    }

    public NotificationGenerationSnapshot generate(String characterId, LivingNotificationEngineInput input) {
        List<NotificationGenerationTraceEntry> trace = new ArrayList<>();
        List<Notification> generated = new ArrayList<>();
        CurrentCharacter user = input.currentUser();
        LocalDate today = LocalDate.ofInstant(input.currentTime(), ZoneId.systemDefault());

        if (properties.isDeveloperForceGeneration()) {
            trace.add(new NotificationGenerationTraceEntry(
                    "developer-force-generation", "Developer force generation enabled"));
            generated.add(buildNotification(
                    user,
                    NotificationType.GREETING,
                    NotificationPriority.MEDIUM,
                    greetingTitle(user),
                    greetingSummary(user, false),
                    "developer-force-greeting",
                    input.currentTime()));
        }

        Optional<RuntimeCharacter> runtimeCharacter = worldRepositoryProvider
                .characters()
                .findById(characterId);
        if (runtimeCharacter.isPresent() && isBirthdayToday(runtimeCharacter.get(), today)) {
            trace.add(new NotificationGenerationTraceEntry("birthday-rule", "Character birthday matches today"));
            generated.add(buildNotification(
                    user,
                    NotificationType.BIRTHDAY,
                    NotificationPriority.HIGH,
                    user.displayName() + "...",
                    "Today is your birthday in the Jungle. Bandar would like to celebrate with you.",
                    "character-birthday",
                    input.currentTime()));
        } else {
            trace.add(new NotificationGenerationTraceEntry(
                    "birthday-rule", "No structured birthday date matched today"));
        }

        findUpcomingFestival(today).ifPresentOrElse(
                festival -> {
                    trace.add(new NotificationGenerationTraceEntry(
                            "festival-rule", "Upcoming festival detected: " + festival.name()));
                    generated.add(buildNotification(
                            user,
                            NotificationType.FESTIVAL,
                            NotificationPriority.HIGH,
                            festival.name(),
                            "A festival is approaching in the Jungle. Bandar has something to share.",
                            "world-festival",
                            input.currentTime()));
                },
                () -> trace.add(new NotificationGenerationTraceEntry(
                        "festival-rule", "No upcoming festival found in world data")));

        if (shouldGenerateGreeting(characterId, input, today, trace)) {
            boolean returningVisitor = isReturningVisitor(input);
            generated.add(buildNotification(
                    user,
                    NotificationType.GREETING,
                    returningVisitor ? NotificationPriority.MEDIUM : NotificationPriority.LOW,
                    greetingTitle(user),
                    greetingSummary(user, returningVisitor),
                    returningVisitor ? "returning-visitor-greeting" : "daily-greeting",
                    input.currentTime()));
        }

        if (generated.isEmpty()) {
            trace.add(new NotificationGenerationTraceEntry(
                    "no-notification", "No notification rules matched; producing nothing"));
        }

        List<Notification> ordered = generated.stream()
                .sorted(Comparator.comparing(Notification::priority).reversed()
                        .thenComparing(Notification::createdAt))
                .toList();
        return new NotificationGenerationSnapshot(characterId, input.currentTime(), trace, ordered);
    }

    private boolean shouldGenerateGreeting(
            String characterId,
            LivingNotificationEngineInput input,
            LocalDate today,
            List<NotificationGenerationTraceEntry> trace) {
        if (alreadyGreetedToday(input.existingNotifications(), today)) {
            trace.add(new NotificationGenerationTraceEntry(
                    "daily-greeting", "Greeting already created today"));
            return false;
        }
        if (isReturningVisitor(input)) {
            trace.add(new NotificationGenerationTraceEntry(
                    "returning-visitor-greeting", "Character has not visited recently"));
            return true;
        }
        int hash = NotificationDeterministicHash.hash(characterId, today.toString(), "daily-greeting");
        int threshold = (int) Math.round(properties.getDailyGreetingProbability() * 100);
        boolean selected = hash % 100 < threshold;
        trace.add(new NotificationGenerationTraceEntry(
                "daily-greeting",
                selected
                        ? "Daily greeting selected deterministically (" + threshold + "% threshold)"
                        : "Daily greeting skipped by probability threshold"));
        return selected;
    }

    private static boolean alreadyGreetedToday(List<Notification> existing, LocalDate today) {
        ZoneId zone = ZoneId.systemDefault();
        return existing.stream()
                .anyMatch(notification -> notification.type() == NotificationType.GREETING
                        && LocalDate.ofInstant(notification.createdAt(), zone).equals(today));
    }

    private boolean isReturningVisitor(LivingNotificationEngineInput input) {
        if (input.lastNotificationAt() == null) {
            return false;
        }
        long days = ChronoUnit.DAYS.between(
                LocalDate.ofInstant(input.lastNotificationAt(), ZoneId.systemDefault()),
                LocalDate.ofInstant(input.currentTime(), ZoneId.systemDefault()));
        return days >= properties.getRecentVisitThresholdDays();
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
            LocalDate parsed = LocalDate.parse("2024-" + normalizeMonthDay(birthday), DateTimeFormatter.ISO_LOCAL_DATE);
            return parsed.getMonthValue() == today.getMonthValue() && parsed.getDayOfMonth() == today.getDayOfMonth();
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    private Optional<FestivalMatch> findUpcomingFestival(LocalDate today) {
        CustomRepository customs = worldRepositoryProvider.customs();
        for (RuntimeCustom custom : customs.findAll()) {
            String festivalDate = custom.sections().get("festivalDate");
            if (festivalDate == null || festivalDate.isBlank()) {
                festivalDate = custom.sections().get("upcomingFestivalDate");
            }
            if (festivalDate == null || festivalDate.isBlank()) {
                continue;
            }
            Optional<LocalDate> parsed = parseMonthDay(festivalDate, today.getYear());
            if (parsed.isEmpty()) {
                continue;
            }
            long daysUntil = ChronoUnit.DAYS.between(today, parsed.get());
            if (daysUntil >= 0 && daysUntil <= 7) {
                return Optional.of(new FestivalMatch(custom.title(), parsed.get()));
            }
        }
        return Optional.empty();
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

    private Notification buildNotification(
            CurrentCharacter user,
            NotificationType type,
            NotificationPriority priority,
            String title,
            String summary,
            String trigger,
            Instant now) {
        Instant expiresAt = now.plus(properties.getNotificationExpirationDays(), ChronoUnit.DAYS);
        return new Notification(
                UUID.randomUUID().toString(),
                user.id(),
                type,
                priority,
                title,
                summary,
                NotificationStatus.PENDING,
                now,
                expiresAt,
                "living-notification-engine",
                trigger,
                Map.of("characterName", user.displayName()));
    }

    private static String greetingTitle(CurrentCharacter user) {
        return user.displayName() + "...";
    }

    private static String greetingSummary(CurrentCharacter user, boolean returningVisitor) {
        if (returningVisitor) {
            return user.displayName() + "...\n\nBandar noticed you have been away. He would like to welcome you back.";
        }
        return user.displayName() + "...\n\nbefore we begin today...";
    }

    private record FestivalMatch(String name, LocalDate date) {}
}
