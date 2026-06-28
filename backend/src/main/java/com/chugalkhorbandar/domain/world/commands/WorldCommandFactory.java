package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class WorldCommandFactory {

    public CreateCharacterCommand createCharacter(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String characterId,
            String title,
            Map<String, String> sections,
            String currentPlaceId,
            String homeTerritoryId) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(characterId, "characterId");
        WorldCommandValidator.requireEntityId(title, "title");
        if (currentPlaceId != null) {
            WorldCommandValidator.requireEntityId(currentPlaceId, "currentPlaceId");
        }
        if (homeTerritoryId != null) {
            WorldCommandValidator.requireEntityId(homeTerritoryId, "homeTerritoryId");
        }
        return new CreateCharacterCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                characterId,
                title,
                sections == null ? Map.of() : sections,
                currentPlaceId,
                homeTerritoryId);
    }

    public UpdateCharacterCommand updateCharacter(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String characterId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(characterId, "characterId");
        return new UpdateCharacterCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                characterId,
                title,
                sections == null ? Map.of() : sections);
    }

    public DeleteCharacterCommand deleteCharacter(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String characterId) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(characterId, "characterId");
        return new DeleteCharacterCommand(
                commandId, correlationId, createdAt, source, initiatedBy, reason, metadata, characterId);
    }

    public CreateTerritoryCommand createTerritory(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String territoryId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(territoryId, "territoryId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreateTerritoryCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                territoryId,
                title,
                sections == null ? Map.of() : sections);
    }

    public TransferTerritoryCommand transferTerritory(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String territoryId,
            String fromRulerId,
            String toRulerId) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(territoryId, "territoryId");
        WorldCommandValidator.requireEntityId(fromRulerId, "fromRulerId");
        WorldCommandValidator.requireEntityId(toRulerId, "toRulerId");
        return new TransferTerritoryCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                territoryId,
                fromRulerId,
                toRulerId);
    }

    public ChangeTerritoryRulerCommand changeTerritoryRuler(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String territoryId,
            String newRulerId) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(territoryId, "territoryId");
        WorldCommandValidator.requireEntityId(newRulerId, "newRulerId");
        return new ChangeTerritoryRulerCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                territoryId,
                newRulerId);
    }

    public CreatePlaceCommand createPlace(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String placeId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(placeId, "placeId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreatePlaceCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                placeId,
                title,
                sections == null ? Map.of() : sections);
    }

    public MoveCharacterCommand moveCharacter(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String characterId,
            String fromPlaceId,
            String toPlaceId) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(characterId, "characterId");
        WorldCommandValidator.requireEntityId(fromPlaceId, "fromPlaceId");
        WorldCommandValidator.requireEntityId(toPlaceId, "toPlaceId");
        return new MoveCharacterCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                characterId,
                fromPlaceId,
                toPlaceId);
    }

    public CreateStoryCommand createStory(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String storyId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(storyId, "storyId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreateStoryCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                storyId,
                title,
                sections == null ? Map.of() : sections);
    }

    public LinkStoryCommand linkStory(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String storyId,
            String linkedStoryId,
            String linkType) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(storyId, "storyId");
        WorldCommandValidator.requireEntityId(linkedStoryId, "linkedStoryId");
        WorldCommandValidator.requireEntityId(linkType, "linkType");
        return new LinkStoryCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                storyId,
                linkedStoryId,
                linkType);
    }

    public CreateRelationshipCommand createRelationship(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String relationshipId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(relationshipId, "relationshipId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreateRelationshipCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                relationshipId,
                title,
                sections == null ? Map.of() : sections);
    }

    public RemoveRelationshipCommand removeRelationship(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String relationshipId) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(relationshipId, "relationshipId");
        return new RemoveRelationshipCommand(
                commandId, correlationId, createdAt, source, initiatedBy, reason, metadata, relationshipId);
    }

    public ChangePreferenceCommand changePreference(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String characterId,
            String preferenceKey,
            String preferenceValue) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(characterId, "characterId");
        WorldCommandValidator.requireEntityId(preferenceKey, "preferenceKey");
        WorldCommandValidator.requireEntityId(preferenceValue, "preferenceValue");
        return new ChangePreferenceCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                characterId,
                preferenceKey,
                preferenceValue);
    }

    public CreateObjectCommand createObject(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String objectId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(objectId, "objectId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreateObjectCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                objectId,
                title,
                sections == null ? Map.of() : sections);
    }

    public TransferObjectCommand transferObject(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String objectId,
            String fromOwnerId,
            String toOwnerId) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(objectId, "objectId");
        WorldCommandValidator.requireEntityId(fromOwnerId, "fromOwnerId");
        WorldCommandValidator.requireEntityId(toOwnerId, "toOwnerId");
        return new TransferObjectCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                objectId,
                fromOwnerId,
                toOwnerId);
    }

    public ConsumeResourceCommand consumeResource(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String resourceId,
            String consumerId,
            int quantity) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(resourceId, "resourceId");
        WorldCommandValidator.requireEntityId(consumerId, "consumerId");
        if (quantity <= 0) {
            throw new WorldCommandValidationException("quantity must be positive");
        }
        return new ConsumeResourceCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                resourceId,
                consumerId,
                quantity);
    }

    public CreateOrganizationCommand createOrganization(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String organizationId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(organizationId, "organizationId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreateOrganizationCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                organizationId,
                title,
                sections == null ? Map.of() : sections);
    }

    public AssignOrganizationRoleCommand assignOrganizationRole(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String organizationId,
            String characterId,
            String role) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(organizationId, "organizationId");
        WorldCommandValidator.requireEntityId(characterId, "characterId");
        WorldCommandValidator.requireEntityId(role, "role");
        return new AssignOrganizationRoleCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                organizationId,
                characterId,
                role);
    }

    public RecordTimelineEntryCommand recordTimelineEntry(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String chronologyId,
            String entryId,
            String title,
            List<TimelineEntry> timelineEntries,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(chronologyId, "chronologyId");
        WorldCommandValidator.requireEntityId(entryId, "entryId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new RecordTimelineEntryCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                chronologyId,
                entryId,
                title,
                timelineEntries == null ? List.of() : timelineEntries,
                sections == null ? Map.of() : sections);
    }

    public CreatePromptProfileCommand createPromptProfile(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String profileId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(profileId, "profileId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreatePromptProfileCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                profileId,
                title,
                sections == null ? Map.of() : sections);
    }

    public CreateLawCommand createLaw(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String lawId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(lawId, "lawId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreateLawCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                lawId,
                title,
                sections == null ? Map.of() : sections);
    }

    public CreateCustomCommand createCustom(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String customId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(customId, "customId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreateCustomCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                customId,
                title,
                sections == null ? Map.of() : sections);
    }

    public CreateGlossaryEntryCommand createGlossaryEntry(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata,
            String glossaryId,
            String title,
            Map<String, String> sections) {
        validateEnvelope(commandId, correlationId, createdAt, source, initiatedBy, reason, metadata);
        WorldCommandValidator.requireEntityId(glossaryId, "glossaryId");
        WorldCommandValidator.requireEntityId(title, "title");
        return new CreateGlossaryEntryCommand(
                commandId,
                correlationId,
                createdAt,
                source,
                initiatedBy,
                reason,
                metadata,
                glossaryId,
                title,
                sections == null ? Map.of() : sections);
    }

    private static void validateEnvelope(
            String commandId,
            String correlationId,
            Instant createdAt,
            CommandSource source,
            String initiatedBy,
            String reason,
            CommandMetadata metadata) {
        WorldCommandValidator.requireCommandId(commandId);
        WorldCommandValidator.requireCorrelationId(correlationId);
        WorldCommandValidator.requireCreatedAt(createdAt);
        WorldCommandValidator.requireSource(source);
        WorldCommandValidator.requireInitiatedBy(initiatedBy);
        WorldCommandValidator.requireReason(reason);
        WorldCommandValidator.requireMetadata(metadata);
    }
}
