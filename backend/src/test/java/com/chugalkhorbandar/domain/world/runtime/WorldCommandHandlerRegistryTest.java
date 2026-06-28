package com.chugalkhorbandar.domain.world.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilation;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompiler;
import com.chugalkhorbandar.bootstrap.compiler.CompilerTestFixtures;
import com.chugalkhorbandar.domain.world.commands.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorldCommandHandlerRegistryTest {

    private final WorldCommandHandlerRegistry registry = WorldCommandHandlerRegistry.createDefault();

    @Test
    void resolvesHandlerForEachCommandType() {
        WorldCommandFactory factory = new WorldCommandFactory();
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        CommandMetadata metadata = CommandMetadata.empty();

        List<WorldCommand> commands = List.of(
                factory.createCharacter(
                        "c1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1", "A", Map.of(), null, null),
                factory.updateCharacter(
                        "c2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1", "B", Map.of()),
                factory.deleteCharacter(
                        "c3", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1"),
                factory.createTerritory(
                        "t1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "ter-1", "T", Map.of()),
                factory.transferTerritory(
                        "t2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "ter-1", "a", "b"),
                factory.changeTerritoryRuler(
                        "t3", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "ter-1", "b"),
                factory.createPlace(
                        "p1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "place-1", "P", Map.of()),
                factory.moveCharacter(
                        "p2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1", "a", "b"),
                factory.createStory(
                        "s1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "story-1", "S", Map.of()),
                factory.linkStory(
                        "s2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "story-1", "story-2", "sequel"),
                factory.createRelationship(
                        "r1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "rel-1", "R", Map.of()),
                factory.removeRelationship(
                        "r2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "rel-1"),
                factory.changePreference(
                        "pr1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1", "food", "mango"),
                factory.createObject(
                        "o1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "obj-1", "O", Map.of()),
                factory.transferObject(
                        "o2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "obj-1", "a", "b"),
                factory.consumeResource(
                        "rs1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "res-1", "char-1", 1),
                factory.createOrganization(
                        "org1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "org-1", "Org", Map.of()),
                factory.assignOrganizationRole(
                        "org2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "org-1", "char-1", "leader"),
                factory.recordTimelineEntry(
                        "tl1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "chrono", "entry", "E", List.of(), Map.of()),
                factory.createPromptProfile(
                        "pp1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "prompt-1", "Prompt", Map.of()),
                factory.createLaw(
                        "l1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "law-1", "Law", Map.of()),
                factory.createCustom(
                        "cu1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "custom-1", "Custom", Map.of()),
                factory.createGlossaryEntry(
                        "g1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "gloss-1", "Gloss", Map.of()));

        assertThat(commands).allSatisfy(command -> assertThat(registry.resolve(command)).isNotNull());
    }

    @Test
    void rejectsUnsupportedCommandType() {
        WorldCommandHandlerRegistry limitedRegistry = new WorldCommandHandlerRegistry(List.of(new OnlyCharacterHandler()));
        WorldCommandFactory factory = new WorldCommandFactory();
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        CreateStoryCommand story = factory.createStory(
                "s1",
                "corr",
                createdAt,
                CommandSource.ADMIN,
                "admin",
                "reason",
                CommandMetadata.empty(),
                "story-1",
                "Story",
                Map.of());

        assertThatThrownBy(() -> limitedRegistry.resolve(story))
                .isInstanceOf(WorldExecutionException.class)
                .hasMessageContaining("No handler registered");
    }

    @Test
    void rejectsNullCommand() {
        assertThatThrownBy(() -> registry.resolve(null))
                .isInstanceOf(WorldExecutionException.class)
                .hasMessageContaining("null command");
    }

    private static final class OnlyCharacterHandler extends AbstractWorldCommandHandler<CreateCharacterCommand> {

        private OnlyCharacterHandler() {
            super(CreateCharacterCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateCharacterCommand command) {
            return current;
        }
    }
}
