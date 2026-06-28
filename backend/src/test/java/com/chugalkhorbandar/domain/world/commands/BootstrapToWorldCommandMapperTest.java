package com.chugalkhorbandar.domain.world.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilation;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompiler;
import com.chugalkhorbandar.bootstrap.compiler.CompilerTestFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;

class BootstrapToWorldCommandMapperTest {

    private final BootstrapCompiler compiler = new BootstrapCompiler();
    private final BootstrapToWorldCommandMapper mapper = new BootstrapToWorldCommandMapper();

    @Test
    void mapsEveryBootstrapCommandToExactlyOneWorldCommand() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        List<WorldCommand> worldCommands = mapper.map(compilation);

        assertThat(worldCommands).hasSize(compilation.commands().size());
    }

    @Test
    void preservesDeterministicOrdering() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        List<String> first = mapper.map(compilation).stream().map(WorldCommand::commandId).toList();
        List<String> second = mapper.map(compilation).stream().map(WorldCommand::commandId).toList();

        assertThat(first).containsExactly(
                "canon",
                "world-rules",
                "prompt_guide",
                "territories",
                "place_home",
                "organizations",
                "resources",
                "objects",
                "character_alpha",
                "character_zeta",
                "relationships",
                "story_origin",
                "world_timeline",
                "laws",
                "customs",
                "glossary");
        assertThat(second).isEqualTo(first);
    }

    @Test
    void mapsBootstrapCommandsToExpectedWorldCommandTypes() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        List<String> types = mapper.map(compilation).stream().map(WorldCommand::commandType).toList();

        assertThat(types).containsExactly(
                "RecordTimelineEntry",
                "CreateLaw",
                "CreatePromptProfile",
                "CreateTerritory",
                "CreatePlace",
                "CreateOrganization",
                "CreateObject",
                "CreateObject",
                "CreateCharacter",
                "CreateCharacter",
                "CreateRelationship",
                "CreateStory",
                "RecordTimelineEntry",
                "CreateLaw",
                "CreateCustom",
                "CreateGlossaryEntry");
    }

    @Test
    void setsBootstrapSourceAndMetadata() {
        BootstrapCompilation compilation = compiler.compile(CompilerTestFixtures.sampleWorld());

        WorldCommand command = mapper.map(compilation).getFirst();

        assertThat(command.source()).isEqualTo(CommandSource.BOOTSTRAP);
        assertThat(command.correlationId()).isEqualTo("bootstrap-compilation");
        assertThat(command.initiatedBy()).isEqualTo("bootstrap-compiler");
        assertThat(command.metadata().get("bootstrapCommandType")).contains("CreateCanon");
        assertThat(command.metadata().get("sourceDocumentId")).contains("canon");
    }

    @Test
    void rejectsNullCompilation() {
        assertThatThrownBy(() -> mapper.map(null)).isInstanceOf(Exception.class);
    }
}
