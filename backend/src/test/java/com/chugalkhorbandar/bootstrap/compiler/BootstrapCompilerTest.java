package com.chugalkhorbandar.bootstrap.compiler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.bootstrap.compiler.command.*;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class BootstrapCompilerTest {

    private final BootstrapCompiler compiler = new BootstrapCompiler();

    @Test
    void generatesCommandsInDeterministicOrder() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        List<String> commandTypes = compilation.commands().stream()
                .map(BootstrapCommand::commandType)
                .toList();

        assertThat(commandTypes).containsExactly(
                "CreateCanon",
                "CreateWorldRules",
                "CreatePromptProfile",
                "CreateTerritory",
                "CreatePlace",
                "CreateOrganization",
                "CreateResource",
                "CreateObject",
                "CreateCharacter",
                "CreateCharacter",
                "CreateRelationship",
                "CreateStory",
                "CreateChronology",
                "CreateLaw",
                "CreateCustom",
                "CreateGlossaryEntry");
    }

    @Test
    void sortsCharactersByIdWithinCategory() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        List<CreateCharacterCommand> characters = compilation.commands().stream()
                .filter(CreateCharacterCommand.class::isInstance)
                .map(CreateCharacterCommand.class::cast)
                .toList();

        assertThat(characters).extracting(CreateCharacterCommand::characterId).containsExactly("character_alpha", "character_zeta");
    }

    @Test
    void identicalInputProducesIdenticalOutput() {
        var world = CompilerTestFixtures.sampleWorld();

        List<String> first = compiler.compile(world).commands().stream()
                .map(BootstrapCommand::commandId)
                .toList();
        List<String> second = compiler.compile(world).commands().stream()
                .map(BootstrapCommand::commandId)
                .toList();

        assertThat(first).isEqualTo(second);
    }

    @Test
    void assignsSequentialExecutionOrder() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        List<Integer> orders = compilation.commands().stream()
                .map(BootstrapCommand::executionOrder)
                .toList();

        assertThat(orders).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
    }

    @Test
    void generatesCompilationStatistics() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        assertThat(compilation.report().success()).isTrue();
        assertThat(compilation.report().totalCommands()).isEqualTo(16);
        assertThat(compilation.report().commandCountsByType().get("Characters")).isEqualTo(2);
        assertThat(compilation.report().toSummary()).contains("Bootstrap Compilation");
    }

    @Test
    void rejectsDuplicateCommandIds() {
        assertThatThrownBy(() -> compiler.compile(CompilerTestFixtures.worldWithDuplicateIds()))
                .isInstanceOf(BootstrapCompilationException.class)
                .hasMessageContaining("Duplicate command id");
    }

    @Test
    void rejectsNullWorld() {
        assertThatThrownBy(() -> compiler.compile(null)).isInstanceOf(BootstrapCompilationException.class);
    }

    @Test
    void commandListIsImmutable() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        assertThatThrownBy(() -> compilation.commands().add(null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void characterCommandContainsSectionsAndMetadata() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        CreateCharacterCommand command = compilation.commands().stream()
                .filter(CreateCharacterCommand.class::isInstance)
                .map(CreateCharacterCommand.class::cast)
                .findFirst()
                .orElseThrow();

        assertThat(command.sections()).containsEntry("summary", "summary");
        assertThat(command.metadata()).containsEntry("status", "ACTIVE");
        assertThat(command.sourceDocumentId()).isEqualTo(command.characterId());
    }

    @Test
    void characterCommandIncludesCurrentPlaceId() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        CreateCharacterCommand command = compilation.commands().stream()
                .filter(CreateCharacterCommand.class::isInstance)
                .map(CreateCharacterCommand.class::cast)
                .filter(character -> "character_alpha".equals(character.characterId()))
                .findFirst()
                .orElseThrow();

        assertThat(command.currentPlaceId()).isEqualTo("place_home");
    }
}
