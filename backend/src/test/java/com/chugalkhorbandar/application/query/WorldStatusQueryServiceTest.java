package com.chugalkhorbandar.application.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryPersistenceProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.runtime.WorldRuntimeStatusHolder;
import com.chugalkhorbandar.bootstrap.BootstrapContext;
import com.chugalkhorbandar.bootstrap.BootstrapContextHolder;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.Manifest;
import com.chugalkhorbandar.bootstrap.model.ValidationReport;
import com.chugalkhorbandar.bootstrap.model.ValidationStatus;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorldStatusQueryServiceTest {

    private final InMemoryWorldStore store = new InMemoryWorldStore();
    private final BootstrapContextHolder bootstrapContextHolder = new BootstrapContextHolder();
    private final WorldRuntimeStatusHolder runtimeStatusHolder = new WorldRuntimeStatusHolder();
    private final WorldStatusQueryService service = new WorldStatusQueryService(
            bootstrapContextHolder,
            new InMemoryWorldRepositoryProvider(store),
            new InMemoryPersistenceProvider(),
            runtimeStatusHolder);

    @BeforeEach
    void seed() {
        bootstrapContextHolder.initialize(new BootstrapContext(
                new BootstrapWorld(
                        Path.of("bootstrap"),
                        Optional.of(new Manifest(
                                "world-1",
                                "Chugalkhor Bandar",
                                "1.0",
                                "1",
                                "test",
                                "2026-06-27",
                                "en",
                                Path.of("bootstrap/manifest.yml"))),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of()),
                new ValidationReport(true, 0, 0, 0, 0, 0, 0, ValidationStatus.VALID, List.of())));

        store.characters()
                .put(
                        "character_a",
                        new RuntimeCharacter("character_a", "A", Map.of(), "place_a", Map.of()));
        store.stories().put("story_a", new RuntimeStory("story_a", "Story A", Map.of(), Map.of()));
        runtimeStatusHolder.markReady();
    }

    @Test
    void getStatusReturnsReadyWithCounts() {
        WorldStatus status = service.getStatus();

        assertThat(status.status()).isEqualTo("READY");
        assertThat(status.bootstrapVersion()).isEqualTo("1.0");
        assertThat(status.bootstrapTimestamp())
                .isEqualTo(LocalDate.parse("2026-06-27").atStartOfDay().toInstant(ZoneOffset.UTC));
        assertThat(status.runtimeStartedAt()).isNotNull();
        assertThat(status.persistenceProvider()).isEqualTo("IN_MEMORY_H2");
        assertThat(status.characters()).isEqualTo(1);
        assertThat(status.stories()).isEqualTo(1);
    }

    @Test
    void getStatusReportsStartingBeforeRuntimeReady() {
        WorldRuntimeStatusHolder startingHolder = new WorldRuntimeStatusHolder();
        WorldStatusQueryService startingService = new WorldStatusQueryService(
                bootstrapContextHolder,
                new InMemoryWorldRepositoryProvider(store),
                new InMemoryPersistenceProvider(),
                startingHolder);

        assertThat(startingService.getStatus().status()).isEqualTo("STARTING");
        assertThat(startingService.getStatus().runtimeStartedAt()).isNull();
    }
}
