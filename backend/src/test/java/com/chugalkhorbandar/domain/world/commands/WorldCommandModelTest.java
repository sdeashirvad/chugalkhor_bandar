package com.chugalkhorbandar.domain.world.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorldCommandModelTest {

    private final WorldCommandFactory factory = new WorldCommandFactory();
    private final Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void commandMetadataIsImmutable() {
        CommandMetadata metadata = CommandMetadata.builder().put("status", "ACTIVE").build();

        assertThat(metadata.asMap()).isUnmodifiable();
        assertThat(metadata.with("version", "1.0").asMap()).containsEntry("status", "ACTIVE");
    }

    @Test
    void sectionsAreImmutableOnCreateCommands() {
        CreateStoryCommand command = factory.createStory(
                "cmd-1",
                "corr-1",
                createdAt,
                CommandSource.IMPORT,
                "importer",
                "import story",
                CommandMetadata.empty(),
                "story-1",
                "Story",
                Map.of("summary", "Once upon a time"));

        assertThatThrownBy(() -> command.sections().put("extra", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void commandsSupportValueEquality() {
        CreatePlaceCommand first = factory.createPlace(
                "cmd-1",
                "corr-1",
                createdAt,
                CommandSource.ADMIN,
                "admin",
                "create place",
                CommandMetadata.empty(),
                "place-1",
                "Place",
                Map.of("description", "A clearing"));

        CreatePlaceCommand second = factory.createPlace(
                "cmd-1",
                "corr-1",
                createdAt,
                CommandSource.ADMIN,
                "admin",
                "create place",
                CommandMetadata.empty(),
                "place-1",
                "Place",
                Map.of("description", "A clearing"));

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void worldCommandEnvelopeFieldsAreAccessible() {
        CreateLawCommand command = factory.createLaw(
                "cmd-law",
                "corr-law",
                createdAt,
                CommandSource.MIGRATION,
                "migrator",
                "migrate law",
                CommandMetadata.of(Map.of("origin", "legacy")),
                "law-1",
                "Law",
                Map.of("description", "No stealing"));

        assertThat(command.commandId()).isEqualTo("cmd-law");
        assertThat(command.correlationId()).isEqualTo("corr-law");
        assertThat(command.createdAt()).isEqualTo(createdAt);
        assertThat(command.source()).isEqualTo(CommandSource.MIGRATION);
        assertThat(command.initiatedBy()).isEqualTo("migrator");
        assertThat(command.reason()).isEqualTo("migrate law");
        assertThat(command.metadata().get("origin")).contains("legacy");
    }
}
