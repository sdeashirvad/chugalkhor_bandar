package com.chugalkhorbandar.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlanner;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentTestSupport;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.query.EntityReferenceResolver;
import com.chugalkhorbandar.application.context.resolver.ContextResolver;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import com.chugalkhorbandar.application.context.resolver.StubContextPermissionChecker;
import com.chugalkhorbandar.bootstrap.typed.BootstrapTypedWorld;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.application.session.SessionStatus;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilation;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompiler;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateCharacterCommand;
import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentReader;
import com.chugalkhorbandar.bootstrap.typed.reader.CharacterBootstrapReader;
import com.chugalkhorbandar.bootstrap.typed.spec.CharacterBootstrapSpec;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.world.commands.BootstrapToWorldCommandMapper;
import com.chugalkhorbandar.domain.world.commands.WorldCommand;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import com.chugalkhorbandar.domain.world.runtime.WorldCommandExecutor;
import com.chugalkhorbandar.domain.world.runtime.WorldRuntime;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CharacterLocationLifecycleTest {

    private static final String CHARACTER_ID = "character_hippu_king";
    private static final String PLACE_ID = "place_hippu_palace";
    private static final String PLACE_TITLE = "Hippu Palace";

    @Test
    void hippuKingLocationSurvivesBootstrapToContextResolver() throws Exception {
        Path bootstrapRoot = Path.of("..", "bootstrap").toAbsolutePath().normalize();
        Path hippuKingFile = bootstrapRoot.resolve("characters/hippu-king.md");
        assertThat(hippuKingFile).exists();

        var document = new BootstrapDocumentReader().read(bootstrapRoot, hippuKingFile);
        CharacterBootstrapSpec typedSpec = new CharacterBootstrapReader().read(document);
        assertThat(typedSpec.currentPlace()).isEqualTo(PLACE_ID);

        BootstrapCompilation compilation = compileHippuKing(typedSpec);
        CreateCharacterCommand bootstrapCommand = findBootstrapCharacterCommand(compilation);
        assertThat(bootstrapCommand.currentPlaceId()).isEqualTo(PLACE_ID);

        List<WorldCommand> worldCommands = new BootstrapToWorldCommandMapper().map(compilation);
        com.chugalkhorbandar.domain.world.commands.CreateCharacterCommand worldCommand =
                worldCommands.stream()
                        .filter(com.chugalkhorbandar.domain.world.commands.CreateCharacterCommand.class::isInstance)
                        .map(com.chugalkhorbandar.domain.world.commands.CreateCharacterCommand.class::cast)
                        .filter(command -> CHARACTER_ID.equals(command.characterId()))
                        .findFirst()
                        .orElseThrow();
        assertThat(worldCommand.currentPlaceId()).isEqualTo(PLACE_ID);

        WorldRuntime runtime = WorldCommandExecutor.createDefault().execute(worldCommands);
        RuntimeCharacter aggregate = runtime.state().characters().get(CHARACTER_ID);
        assertThat(aggregate.currentPlaceId()).isEqualTo(PLACE_ID);

        InMemoryWorldStore store = new InMemoryWorldStore();
        store.places().put(PLACE_ID, new RuntimePlace(
                PLACE_ID,
                PLACE_TITLE,
                Map.of("type", "Royal Residence", "locatedIn", "Home Jungle", "description", "Official residence of Hippu King.")));
        store.places().put("place_home_jungle", new RuntimePlace(
                "place_home_jungle",
                "Home Jungle",
                Map.of("type", "Capital Jungle", "description", "Home Jungle is the capital of the Hippu Dynasty.")));
        store.characters().put(CHARACTER_ID, aggregate);
        InMemoryWorldRepositoryProvider repositories = new InMemoryWorldRepositoryProvider(store);

        RuntimeCharacter persisted = repositories.characters().findById(CHARACTER_ID).orElseThrow();
        assertThat(persisted.currentPlaceId()).isEqualTo(PLACE_ID);

        EntityReferenceResolver referenceResolver = new EntityReferenceResolver(repositories);
        com.chugalkhorbandar.application.query.CharacterQueryService queryService =
                new com.chugalkhorbandar.application.query.CharacterQueryService(repositories, referenceResolver);
        assertThat(queryService.findById(CHARACTER_ID).currentPlaceId()).isEqualTo(PLACE_ID);

        store.promptProfiles()
                .put(
                        "prompt_bandar_personality",
                        new com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile(
                                "prompt_bandar_personality", "Bandar", Map.of("identity", "Bandar")));
        ContextPlanner planner = new ContextPlanner(KnowledgeFragmentTestSupport.fragmentPlanner(repositories));
        ContextResolver resolver = new ContextResolver(KnowledgeFragmentTestSupport.fragmentResolver(repositories));
        ContextPlannerRequest request = hippuKingRequest("Where am I in the Jungle?");

        ContextPlan plan = planner.plan(request);
        assertThat(plan.fragmentPlan().requests().stream().map(item -> item.fragmentType()))
                .contains(KnowledgeFragmentType.CHARACTER_LOCATION);

        ResolvedContext resolved = resolver.resolve(plan, request);
        var locationFragment = resolved.fragments().stream()
                .filter(fragment -> fragment.fragmentType() == KnowledgeFragmentType.CHARACTER_LOCATION)
                .findFirst()
                .orElseThrow();
        assertThat(locationFragment.content()).contains(PLACE_TITLE);
        assertThat(locationFragment.content()).contains("Home Jungle");
        assertThat(locationFragment.content()).isNotEqualTo("Current location is unknown.");
        assertThat(locationFragment.content()).doesNotContain("[missing:");
    }

    @Test
    void diagnosticTraceReportsCurrentPlaceAtEachStage() throws Exception {
        Path bootstrapRoot = Path.of("..", "bootstrap").toAbsolutePath().normalize();
        Path hippuKingFile = bootstrapRoot.resolve("characters/hippu-king.md");
        var document = new BootstrapDocumentReader().read(bootstrapRoot, hippuKingFile);
        CharacterBootstrapSpec typedSpec = new CharacterBootstrapReader().read(document);
        BootstrapCompilation compilation = compileHippuKing(typedSpec);
        CreateCharacterCommand bootstrapCommand = findBootstrapCharacterCommand(compilation);
        List<WorldCommand> worldCommands = new BootstrapToWorldCommandMapper().map(compilation);
        WorldRuntime runtime = WorldCommandExecutor.createDefault().execute(worldCommands);

        CharacterLocationDiagnostic trace = CharacterLocationDiagnostic.trace(
                typedSpec.currentPlace(),
                bootstrapCommand.currentPlaceId(),
                worldCommands.stream()
                        .filter(com.chugalkhorbandar.domain.world.commands.CreateCharacterCommand.class::isInstance)
                        .map(com.chugalkhorbandar.domain.world.commands.CreateCharacterCommand.class::cast)
                        .filter(command -> CHARACTER_ID.equals(command.characterId()))
                        .findFirst()
                        .orElseThrow()
                        .currentPlaceId(),
                runtime.state().characters().get(CHARACTER_ID).currentPlaceId(),
                null,
                null,
                null);

        assertThat(trace.typedModel()).isEqualTo(PLACE_ID);
        assertThat(trace.bootstrapCommand()).isEqualTo(PLACE_ID);
        assertThat(trace.worldCommand()).isEqualTo(PLACE_ID);
        assertThat(trace.runtimeAggregate()).isEqualTo(PLACE_ID);
        assertThat(trace.format()).contains("typed model").contains(PLACE_ID);
    }

    private static BootstrapCompilation compileHippuKing(CharacterBootstrapSpec hippuKing) {
        return new BootstrapCompiler()
                .compile(BootstrapTypedWorld.builder().addCharacter(hippuKing).build());
    }

    private static CreateCharacterCommand findBootstrapCharacterCommand(BootstrapCompilation compilation) {
        return compilation.commands().stream()
                .filter(CreateCharacterCommand.class::isInstance)
                .map(CreateCharacterCommand.class::cast)
                .filter(command -> CHARACTER_ID.equals(command.characterId()))
                .findFirst()
                .orElseThrow();
    }

    private static ContextPlannerRequest hippuKingRequest(String latestMessage) {
        CurrentCharacter character = new CurrentCharacter(CHARACTER_ID, "Hippu King", List.of("King"), "Hippu", "Hippu Kingdom", null);
        ChatSession session = new ChatSession(
                "session-1",
                character,
                Instant.parse("2026-06-27T12:00:00Z"),
                Instant.parse("2026-06-27T12:00:00Z"),
                SessionStatus.ACTIVE);
        Conversation conversation = new Conversation(
                "conversation-1",
                "session-1",
                new ConversationCharacter(CHARACTER_ID, "Hippu King", List.of("King"), "Hippu Kingdom", null),
                Instant.parse("2026-06-27T12:00:00Z"),
                Instant.parse("2026-06-27T12:00:00Z"),
                ConversationStatus.ACTIVE,
                List.of());
        return new ContextPlannerRequest(
                character,
                session,
                conversation,
                latestMessage,
                new com.chugalkhorbandar.application.context.RuntimeWorldContext("READY", "1.0", 13, 3, List.of()));
    }
}
