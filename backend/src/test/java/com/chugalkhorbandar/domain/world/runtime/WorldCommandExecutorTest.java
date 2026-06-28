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

class WorldCommandExecutorTest {

    private final WorldCommandExecutor executor = WorldCommandExecutor.createDefault();
    private final WorldCommandFactory factory = new WorldCommandFactory();
    private final Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void executesBootstrapPipelineIntoRuntimeWorld() {
        BootstrapCompilation compilation = new BootstrapCompiler().compile(CompilerTestFixtures.sampleWorld());
        List<WorldCommand> commands = new BootstrapToWorldCommandMapper().map(compilation);

        WorldRuntime runtime = executor.execute(commands);

        assertThat(runtime.report().success()).isTrue();
        assertThat(runtime.report().executedCommandCount()).isEqualTo(16);
        assertThat(runtime.state().characters()).hasSize(2);
        assertThat(runtime.state().stories()).hasSize(1);
        assertThat(runtime.state().relationships()).hasSize(1);
        assertThat(runtime.state().canon()).hasSize(1);
        assertThat(runtime.state().timeline()).hasSize(1);
        assertThat(runtime.state().worldRules()).hasSize(1);
        assertThat(runtime.state().laws()).hasSize(1);
    }

    @Test
    void preservesCommandOrdering() {
        CommandMetadata metadata = CommandMetadata.empty();
        List<WorldCommand> commands = List.of(
                factory.createTerritory(
                        "t1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "ter-1", "T", Map.of()),
                factory.createCharacter(
                        "c1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1", "C", Map.of(), null, null),
                factory.createStory(
                        "s1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "story-1", "S", Map.of()));

        WorldRuntime runtime = executor.execute(commands);

        assertThat(runtime.state().territories()).containsKey("ter-1");
        assertThat(runtime.state().characters()).containsKey("char-1");
        assertThat(runtime.state().stories()).containsKey("story-1");
    }

    @Test
    void createsCharacterWithInitialPlace() {
        CommandMetadata metadata = CommandMetadata.empty();
        CreateCharacterCommand create = factory.createCharacter(
                "c1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1", "Alpha", Map.of(), "place_home", null);

        WorldRuntime runtime = executor.execute(List.of(create));

        assertThat(runtime.state().characters().get("char-1").currentPlaceId()).isEqualTo("place_home");
    }

    @Test
    void updatesAreImmutableAndDoNotMutatePreviousState() {
        CommandMetadata metadata = CommandMetadata.empty();
        CreateCharacterCommand create = factory.createCharacter(
                "c1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1", "Alpha", Map.of(), null, null);
        MoveCharacterCommand move = factory.moveCharacter(
                "c2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "char-1", "a", "b");

        WorldRuntime afterCreate = executor.execute(List.of(create));
        WorldRuntime afterMove = executor.execute(List.of(create, move));

        assertThat(afterCreate.state().characters().get("char-1").currentPlaceId()).isNull();
        assertThat(afterMove.state().characters().get("char-1").currentPlaceId()).isEqualTo("b");
        assertThat(afterCreate.state().characters().get("char-1").currentPlaceId()).isNull();
    }

    @Test
    void rejectsDuplicateRuntimeIds() {
        CommandMetadata metadata = CommandMetadata.empty();
        CreateCharacterCommand first = factory.createCharacter(
                "c1", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "dup", "First", Map.of(), null, null);
        CreateCharacterCommand second = factory.createCharacter(
                "c2", "corr", createdAt, CommandSource.ADMIN, "admin", "reason", metadata, "dup", "Second", Map.of(), null, null);

        assertThatThrownBy(() -> executor.execute(List.of(first, second)))
                .isInstanceOf(WorldExecutionException.class)
                .hasMessageContaining("Duplicate runtime id");
    }

    @Test
    void generatesExecutionReportWithStatistics() {
        BootstrapCompilation compilation = new BootstrapCompiler().compile(CompilerTestFixtures.sampleWorld());
        List<WorldCommand> commands = new BootstrapToWorldCommandMapper().map(compilation);

        WorldExecutionReport report = executor.execute(commands).report();

        assertThat(report.executedCommandCount()).isEqualTo(16);
        assertThat(report.statistics().get("Characters")).isEqualTo(2);
        assertThat(report.toSummary()).contains("World Execution");
    }

    @Test
    void rejectsNullCommandInList() {
        java.util.ArrayList<WorldCommand> commands = new java.util.ArrayList<>();
        commands.add(null);

        assertThatThrownBy(() -> executor.execute(commands))
                .isInstanceOf(WorldExecutionException.class)
                .hasMessageContaining("null command");
    }
}
