package com.chugalkhorbandar.domain.world.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilation;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompiler;
import com.chugalkhorbandar.bootstrap.compiler.CompilerTestFixtures;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorldCommandFactoryTest {

    private final WorldCommandFactory factory = new WorldCommandFactory();
    private final Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void createsCharacterCommand() {
        CreateCharacterCommand command = factory.createCharacter(
                "cmd-1",
                "corr-1",
                createdAt,
                CommandSource.ADMIN,
                "admin",
                "create character",
                CommandMetadata.empty(),
                "character_alpha",
                "Alpha",
                Map.of("summary", "A leader"),
                null,
                null);

        assertThat(command.commandType()).isEqualTo("CreateCharacter");
        assertThat(command.characterId()).isEqualTo("character_alpha");
        assertThat(command.sections()).containsEntry("summary", "A leader");
    }

    @Test
    void rejectsMissingSource() {
        assertThatThrownBy(() -> factory.createCharacter(
                        "cmd-1",
                        "corr-1",
                        createdAt,
                        null,
                        "admin",
                        "create character",
                        CommandMetadata.empty(),
                        "character_alpha",
                        "Alpha",
                        Map.of(),
                        null,
                        null))
                .isInstanceOf(WorldCommandValidationException.class)
                .hasMessageContaining("source");
    }

    @Test
    void rejectsBlankCommandId() {
        assertThatThrownBy(() -> factory.transferTerritory(
                        "",
                        "corr-1",
                        createdAt,
                        CommandSource.SYSTEM,
                        "system",
                        "transfer",
                        CommandMetadata.empty(),
                        "territory-1",
                        "ruler-a",
                        "ruler-b"))
                .isInstanceOf(WorldCommandValidationException.class)
                .hasMessageContaining("commandId");
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> factory.consumeResource(
                        "cmd-1",
                        "corr-1",
                        createdAt,
                        CommandSource.CHAT,
                        "player",
                        "consume",
                        CommandMetadata.empty(),
                        "resource-1",
                        "character-1",
                        0))
                .isInstanceOf(WorldCommandValidationException.class)
                .hasMessageContaining("quantity");
    }
}
