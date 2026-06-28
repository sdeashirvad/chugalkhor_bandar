package com.chugalkhorbandar.domain.world.commands;

import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilation;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilationException;
import com.chugalkhorbandar.bootstrap.compiler.command.BootstrapCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateCanonCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateChronologyCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateCharacterCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateCustomCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateGlossaryEntryCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateLawCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateObjectCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateOrganizationCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreatePlaceCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreatePromptProfileCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateRelationshipCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateResourceCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateStoryCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateTerritoryCommand;
import com.chugalkhorbandar.bootstrap.compiler.command.CreateWorldRulesCommand;
import com.chugalkhorbandar.bootstrap.typed.spec.ChronologyTimelineItem;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BootstrapToWorldCommandMapper {

    private static final String BOOTSTRAP_INITIATOR = "bootstrap-compiler";
    private static final String BOOTSTRAP_REASON = "Initial world seed from bootstrap canon";
    private static final String CORRELATION_PREFIX = "bootstrap-compilation";

    private final WorldCommandFactory factory = new WorldCommandFactory();

    public List<WorldCommand> map(BootstrapCompilation compilation) {
        if (compilation == null) {
            throw new BootstrapCompilationException("Cannot map null BootstrapCompilation");
        }

        String correlationId = CORRELATION_PREFIX;
        List<WorldCommand> commands = new ArrayList<>();

        for (BootstrapCommand bootstrapCommand : compilation.commands()) {
            commands.add(mapCommand(bootstrapCommand, correlationId));
        }

        return List.copyOf(commands);
    }

    private WorldCommand mapCommand(BootstrapCommand bootstrapCommand, String correlationId) {
        Instant createdAt = deterministicCreatedAt(bootstrapCommand.executionOrder());
        CommandMetadata metadata = bootstrapMetadata(bootstrapCommand);

        return switch (bootstrapCommand) {
            case CreateCanonCommand command -> factory.recordTimelineEntry(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata.with("bootstrapCommandType", "CreateCanon"),
                    command.canonId(),
                    command.canonId(),
                    command.title(),
                    List.of(),
                    command.sections());
            case CreateWorldRulesCommand command -> factory.createLaw(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata.with("bootstrapCommandType", "CreateWorldRules"),
                    command.worldRulesId(),
                    command.title(),
                    command.sections());
            case CreatePromptProfileCommand command -> factory.createPromptProfile(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.promptProfileId(),
                    command.title(),
                    command.sections());
            case CreateTerritoryCommand command -> factory.createTerritory(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.territoryId(),
                    command.title(),
                    command.sections());
            case CreatePlaceCommand command -> factory.createPlace(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.placeId(),
                    command.title(),
                    command.sections());
            case CreateOrganizationCommand command -> factory.createOrganization(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.organizationId(),
                    command.title(),
                    command.sections());
            case CreateResourceCommand command -> factory.createObject(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata.with("bootstrapCommandType", "CreateResource"),
                    command.resourceId(),
                    command.title(),
                    command.sections());
            case CreateObjectCommand command -> factory.createObject(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.objectId(),
                    command.title(),
                    command.sections());
            case CreateCharacterCommand command -> factory.createCharacter(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.characterId(),
                    command.title(),
                    command.sections(),
                    command.currentPlaceId(),
                    command.homeTerritoryId());
            case CreateRelationshipCommand command -> factory.createRelationship(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.relationshipId(),
                    command.title(),
                    command.sections());
            case CreateStoryCommand command -> factory.createStory(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.storyId(),
                    command.title(),
                    command.sections());
            case CreateChronologyCommand command -> factory.recordTimelineEntry(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata.with("bootstrapCommandType", "CreateChronology"),
                    command.chronologyId(),
                    command.chronologyId(),
                    command.title(),
                    toTimelineEntries(command.timelineItems()),
                    command.sections());
            case CreateLawCommand command -> factory.createLaw(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.lawId(),
                    command.title(),
                    command.sections());
            case CreateCustomCommand command -> factory.createCustom(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.customId(),
                    command.title(),
                    command.sections());
            case CreateGlossaryEntryCommand command -> factory.createGlossaryEntry(
                    command.commandId(),
                    correlationId,
                    createdAt,
                    CommandSource.BOOTSTRAP,
                    BOOTSTRAP_INITIATOR,
                    BOOTSTRAP_REASON,
                    metadata,
                    command.glossaryEntryId(),
                    command.title(),
                    command.sections());
        };
    }

    private static Instant deterministicCreatedAt(int executionOrder) {
        return Instant.EPOCH.plusSeconds(executionOrder);
    }

    private static CommandMetadata bootstrapMetadata(BootstrapCommand command) {
        CommandMetadata.Builder builder = CommandMetadata.builder()
                .put("sourceDocumentId", command.sourceDocumentId())
                .put("sourcePath", command.sourcePath().toString())
                .put("executionOrder", String.valueOf(command.executionOrder()))
                .put("bootstrapCommandType", command.commandType());
        return builder.build();
    }

    private static List<TimelineEntry> toTimelineEntries(List<ChronologyTimelineItem> items) {
        return items.stream()
                .map(item -> new TimelineEntry(
                        item.era(),
                        item.approximateDate(),
                        item.event(),
                        item.description(),
                        item.linkedStory()))
                .toList();
    }
}
