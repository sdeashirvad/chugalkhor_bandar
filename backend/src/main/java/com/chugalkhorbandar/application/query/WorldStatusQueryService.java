package com.chugalkhorbandar.application.query;

import com.chugalkhorbandar.application.runtime.WorldRuntimeStatusHolder;
import com.chugalkhorbandar.bootstrap.BootstrapContextHolder;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.ports.query.RelationshipQuery;
import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import com.chugalkhorbandar.domain.world.ports.query.TimelineQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTimelineEntry;
import com.chugalkhorbandar.ports.PersistenceProvider;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorldStatusQueryService {

    private final BootstrapContextHolder bootstrapContextHolder;
    private final WorldRepositoryProvider repositoryProvider;
    private final PersistenceProvider persistenceProvider;
    private final WorldRuntimeStatusHolder runtimeStatusHolder;

    public WorldStatusQueryService(
            BootstrapContextHolder bootstrapContextHolder,
            WorldRepositoryProvider repositoryProvider,
            PersistenceProvider persistenceProvider,
            WorldRuntimeStatusHolder runtimeStatusHolder) {
        this.bootstrapContextHolder = bootstrapContextHolder;
        this.repositoryProvider = repositoryProvider;
        this.persistenceProvider = persistenceProvider;
        this.runtimeStatusHolder = runtimeStatusHolder;
    }

    public WorldStatus getStatus() {
        String bootstrapVersion = bootstrapContextHolder.getRequired().getManifest().bootstrapVersion();
        Instant bootstrapTimestamp = parseBootstrapTimestamp(
                bootstrapContextHolder.getRequired().getManifest().createdAt());

        List<RuntimeCharacter> allCharacters =
                repositoryProvider.characters().findAll(CharacterQuery.all());
        List<RuntimeStory> allStories = repositoryProvider.stories().findAll(StoryQuery.all());
        List<RuntimeTimelineEntry> timelineEntries =
                repositoryProvider.timeline().findAll(TimelineQuery.all());

        return new WorldStatus(
                runtimeStatusHolder.isReady() ? "READY" : "STARTING",
                bootstrapVersion,
                bootstrapTimestamp,
                runtimeStatusHolder.runtimeStartedAt(),
                persistenceProvider.getType().name(),
                allCharacters.size(),
                allStories.size(),
                repositoryProvider.territories().findAll().size(),
                repositoryProvider.places().findAll().size(),
                repositoryProvider.organizations().findAll().size(),
                repositoryProvider.relationships().findAll(RelationshipQuery.all()).size(),
                countTimelineItems(timelineEntries),
                charactersBySpecies(allCharacters),
                storiesByEra(allStories, timelineEntries));
    }

    private static int countTimelineItems(List<RuntimeTimelineEntry> entries) {
        return entries.stream().mapToInt(entry -> entry.timelineEntries().size()).sum();
    }

    private static Map<String, Integer> charactersBySpecies(List<RuntimeCharacter> characters) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (RuntimeCharacter character : characters) {
            String species = TextSectionSupport.extractSpecies(character.sections());
            if (species == null || species.isBlank()) {
                species = "Unknown";
            }
            counts.merge(species, 1, Integer::sum);
        }
        return Map.copyOf(counts);
    }

    private static Map<String, Integer> storiesByEra(
            List<RuntimeStory> stories, List<RuntimeTimelineEntry> timelineEntries) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (RuntimeStory story : stories) {
            String era = story.sections().getOrDefault("era", "Uncategorized");
            if (era.isBlank()) {
                era = "Uncategorized";
            }
            counts.merge(era, 1, Integer::sum);
        }
        return Map.copyOf(counts);
    }

    private static Instant parseBootstrapTimestamp(String createdAt) {
        if (createdAt == null || createdAt.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(createdAt).atStartOfDay().toInstant(ZoneOffset.UTC);
        } catch (RuntimeException exception) {
            return Instant.parse(createdAt);
        }
    }
}
