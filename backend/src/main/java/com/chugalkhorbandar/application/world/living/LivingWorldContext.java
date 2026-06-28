package com.chugalkhorbandar.application.world.living;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.chronicle.Chronicle;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record LivingWorldContext(
        Instant now,
        LocalDate today,
        WorldClockMode mode,
        List<String> allCharacterIds,
        List<ConversationArtifact> activeArtifacts,
        List<ConversationArtifact> allArtifacts,
        List<Chronicle> chronicles,
        List<WorldEvent> generatedEvents,
        Set<String> existingEventIds) {}
