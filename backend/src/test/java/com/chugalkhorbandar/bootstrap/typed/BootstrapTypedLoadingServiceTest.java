package com.chugalkhorbandar.bootstrap.typed;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.typed.reader.*;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BootstrapTypedLoadingServiceTest {

    private final BootstrapTypedLoadingService loadingService = new BootstrapTypedLoadingService(
            new BootstrapTypedReaderRegistry(List.of(
                    new CharacterBootstrapReader(),
                    new StoryBootstrapReader(),
                    new PlaceBootstrapReader(),
                    new TerritoryBootstrapReader(),
                    new OrganizationBootstrapReader(),
                    new ResourceBootstrapReader(),
                    new ObjectBootstrapReader(),
                    new RelationshipBootstrapReader(),
                    new LawBootstrapReader(),
                    new CustomBootstrapReader(),
                    new GlossaryBootstrapReader(),
                    new PromptProfileBootstrapReader(),
                    new CanonBootstrapReader(),
                    new WorldRulesBootstrapReader(),
                    new ChronologyBootstrapReader())));

    @Test
    void aggregatesTypedWorld(@TempDir Path tempDir) throws Exception {
        var repository = TypedBootstrapTestFixtures.loadRepository(tempDir);

        BootstrapTypedWorld world = loadingService.load(repository);

        assertThat(world.characters()).hasSize(1);
        assertThat(world.stories()).hasSize(1);
        assertThat(world.promptProfiles()).hasSize(1);
        assertThat(world.chronologyEntries()).hasSize(1);
        assertThat(world.places()).isEmpty();
        assertThat(world.canon()).hasSize(1);
        assertThat(world.characters().getFirst().summary()).isEqualTo("A brave hero.");
    }
}
