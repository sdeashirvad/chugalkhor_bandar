package com.chugalkhorbandar.domain.world.runtime;

import com.chugalkhorbandar.domain.world.commands.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class WorldCommandHandlers {

    private WorldCommandHandlers() {}

    static List<WorldCommandHandler<?>> all() {
        return List.of(
                new CreateCharacterCommandHandler(),
                new UpdateCharacterCommandHandler(),
                new DeleteCharacterCommandHandler(),
                new CreateTerritoryCommandHandler(),
                new TransferTerritoryCommandHandler(),
                new ChangeTerritoryRulerCommandHandler(),
                new CreatePlaceCommandHandler(),
                new MoveCharacterCommandHandler(),
                new CreateStoryCommandHandler(),
                new LinkStoryCommandHandler(),
                new CreateRelationshipCommandHandler(),
                new RemoveRelationshipCommandHandler(),
                new ChangePreferenceCommandHandler(),
                new CreateObjectCommandHandler(),
                new TransferObjectCommandHandler(),
                new ConsumeResourceCommandHandler(),
                new CreateOrganizationCommandHandler(),
                new AssignOrganizationRoleCommandHandler(),
                new RecordTimelineEntryCommandHandler(),
                new CreatePromptProfileCommandHandler(),
                new CreateLawCommandHandler(),
                new CreateCustomCommandHandler(),
                new CreateGlossaryEntryCommandHandler());
    }

    private static final class CreateCharacterCommandHandler
            extends AbstractWorldCommandHandler<CreateCharacterCommand> {

        CreateCharacterCommandHandler() {
            super(CreateCharacterCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateCharacterCommand command) {
            Map<String, String> preferences = new java.util.LinkedHashMap<>();
            if (command.homeTerritoryId() != null) {
                preferences.put("homeTerritoryId", command.homeTerritoryId());
            }
            return current.addCharacter(new RuntimeCharacter(
                    command.characterId(),
                    command.title(),
                    command.sections(),
                    command.currentPlaceId(),
                    Map.copyOf(preferences)));
        }
    }

    private static final class UpdateCharacterCommandHandler
            extends AbstractWorldCommandHandler<UpdateCharacterCommand> {

        UpdateCharacterCommandHandler() {
            super(UpdateCharacterCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, UpdateCharacterCommand command) {
            return current.updateCharacter(command.characterId(), existing -> {
                Map<String, String> merged = new LinkedHashMap<>(existing.sections());
                merged.putAll(command.sections());
                String title = command.title() == null || command.title().isBlank()
                        ? existing.title()
                        : command.title();
                return existing.withTitle(title).withSections(Map.copyOf(merged));
            });
        }
    }

    private static final class DeleteCharacterCommandHandler
            extends AbstractWorldCommandHandler<DeleteCharacterCommand> {

        DeleteCharacterCommandHandler() {
            super(DeleteCharacterCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, DeleteCharacterCommand command) {
            return current.removeCharacter(command.characterId());
        }
    }

    private static final class CreateTerritoryCommandHandler
            extends AbstractWorldCommandHandler<CreateTerritoryCommand> {

        CreateTerritoryCommandHandler() {
            super(CreateTerritoryCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateTerritoryCommand command) {
            String ruler = command.sections().get("currentRuler");
            return current.addTerritory(new RuntimeTerritory(
                    command.territoryId(), command.title(), command.sections(), ruler));
        }
    }

    private static final class TransferTerritoryCommandHandler
            extends AbstractWorldCommandHandler<TransferTerritoryCommand> {

        TransferTerritoryCommandHandler() {
            super(TransferTerritoryCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, TransferTerritoryCommand command) {
            return current.updateTerritory(
                    command.territoryId(), territory -> territory.withCurrentRulerId(command.toRulerId()));
        }
    }

    private static final class ChangeTerritoryRulerCommandHandler
            extends AbstractWorldCommandHandler<ChangeTerritoryRulerCommand> {

        ChangeTerritoryRulerCommandHandler() {
            super(ChangeTerritoryRulerCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, ChangeTerritoryRulerCommand command) {
            return current.updateTerritory(
                    command.territoryId(), territory -> territory.withCurrentRulerId(command.newRulerId()));
        }
    }

    private static final class CreatePlaceCommandHandler extends AbstractWorldCommandHandler<CreatePlaceCommand> {

        CreatePlaceCommandHandler() {
            super(CreatePlaceCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreatePlaceCommand command) {
            return current.addPlace(new RuntimePlace(command.placeId(), command.title(), command.sections()));
        }
    }

    private static final class MoveCharacterCommandHandler extends AbstractWorldCommandHandler<MoveCharacterCommand> {

        MoveCharacterCommandHandler() {
            super(MoveCharacterCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, MoveCharacterCommand command) {
            return current.updateCharacter(
                    command.characterId(),
                    character -> character.withCurrentPlaceId(command.toPlaceId()));
        }
    }

    private static final class CreateStoryCommandHandler extends AbstractWorldCommandHandler<CreateStoryCommand> {

        CreateStoryCommandHandler() {
            super(CreateStoryCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateStoryCommand command) {
            return current.addStory(new RuntimeStory(
                    command.storyId(), command.title(), command.sections(), Map.of()));
        }
    }

    private static final class LinkStoryCommandHandler extends AbstractWorldCommandHandler<LinkStoryCommand> {

        LinkStoryCommandHandler() {
            super(LinkStoryCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, LinkStoryCommand command) {
            return current.updateStory(
                    command.storyId(),
                    story -> story.withLinkedStory(command.linkedStoryId(), command.linkType()));
        }
    }

    private static final class CreateRelationshipCommandHandler
            extends AbstractWorldCommandHandler<CreateRelationshipCommand> {

        CreateRelationshipCommandHandler() {
            super(CreateRelationshipCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateRelationshipCommand command) {
            return current.addRelationship(new RuntimeRelationship(
                    command.relationshipId(), command.title(), command.sections()));
        }
    }

    private static final class RemoveRelationshipCommandHandler
            extends AbstractWorldCommandHandler<RemoveRelationshipCommand> {

        RemoveRelationshipCommandHandler() {
            super(RemoveRelationshipCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, RemoveRelationshipCommand command) {
            return current.removeRelationship(command.relationshipId());
        }
    }

    private static final class ChangePreferenceCommandHandler
            extends AbstractWorldCommandHandler<ChangePreferenceCommand> {

        ChangePreferenceCommandHandler() {
            super(ChangePreferenceCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, ChangePreferenceCommand command) {
            return current.updateCharacter(
                    command.characterId(),
                    character -> character.withPreference(command.preferenceKey(), command.preferenceValue()));
        }
    }

    private static final class CreateObjectCommandHandler extends AbstractWorldCommandHandler<CreateObjectCommand> {

        CreateObjectCommandHandler() {
            super(CreateObjectCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateObjectCommand command) {
            if (HandlerSupport.isBootstrapType(command, "CreateResource")) {
                return current.addResource(new RuntimeResource(
                        command.objectId(), command.title(), command.sections(), 0));
            }
            String owner = command.sections().get("owner");
            return current.addObject(new RuntimeObject(
                    command.objectId(), command.title(), command.sections(), owner));
        }
    }

    private static final class TransferObjectCommandHandler
            extends AbstractWorldCommandHandler<TransferObjectCommand> {

        TransferObjectCommandHandler() {
            super(TransferObjectCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, TransferObjectCommand command) {
            return current.updateObject(
                    command.objectId(), object -> object.withOwnerId(command.toOwnerId()));
        }
    }

    private static final class ConsumeResourceCommandHandler
            extends AbstractWorldCommandHandler<ConsumeResourceCommand> {

        ConsumeResourceCommandHandler() {
            super(ConsumeResourceCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, ConsumeResourceCommand command) {
            return current.updateResource(command.resourceId(), resource -> {
                int remaining = resource.availableQuantity() - command.quantity();
                return resource.withAvailableQuantity(Math.max(0, remaining));
            });
        }
    }

    private static final class CreateOrganizationCommandHandler
            extends AbstractWorldCommandHandler<CreateOrganizationCommand> {

        CreateOrganizationCommandHandler() {
            super(CreateOrganizationCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateOrganizationCommand command) {
            return current.addOrganization(new RuntimeOrganization(
                    command.organizationId(), command.title(), command.sections(), Map.of()));
        }
    }

    private static final class AssignOrganizationRoleCommandHandler
            extends AbstractWorldCommandHandler<AssignOrganizationRoleCommand> {

        AssignOrganizationRoleCommandHandler() {
            super(AssignOrganizationRoleCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, AssignOrganizationRoleCommand command) {
            return current.updateOrganization(
                    command.organizationId(),
                    organization -> organization.withRole(command.characterId(), command.role()));
        }
    }

    private static final class RecordTimelineEntryCommandHandler
            extends AbstractWorldCommandHandler<RecordTimelineEntryCommand> {

        RecordTimelineEntryCommandHandler() {
            super(RecordTimelineEntryCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, RecordTimelineEntryCommand command) {
            if (HandlerSupport.isBootstrapType(command, "CreateCanon")) {
                return current.addCanon(new RuntimeCanon(command.entryId(), command.title(), command.sections()));
            }
            return current.addTimelineEntry(new RuntimeTimelineEntry(
                    command.entryId(),
                    command.chronologyId(),
                    command.title(),
                    command.timelineEntries(),
                    command.sections()));
        }
    }

    private static final class CreatePromptProfileCommandHandler
            extends AbstractWorldCommandHandler<CreatePromptProfileCommand> {

        CreatePromptProfileCommandHandler() {
            super(CreatePromptProfileCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreatePromptProfileCommand command) {
            return current.addPromptProfile(new RuntimePromptProfile(
                    command.profileId(), command.title(), command.sections()));
        }
    }

    private static final class CreateLawCommandHandler extends AbstractWorldCommandHandler<CreateLawCommand> {

        CreateLawCommandHandler() {
            super(CreateLawCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateLawCommand command) {
            if (HandlerSupport.isBootstrapType(command, "CreateWorldRules")) {
                return current.addWorldRules(new RuntimeWorldRules(
                        command.lawId(), command.title(), command.sections()));
            }
            return current.addLaw(new RuntimeLaw(command.lawId(), command.title(), command.sections()));
        }
    }

    private static final class CreateCustomCommandHandler extends AbstractWorldCommandHandler<CreateCustomCommand> {

        CreateCustomCommandHandler() {
            super(CreateCustomCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateCustomCommand command) {
            return current.addCustom(new RuntimeCustom(command.customId(), command.title(), command.sections()));
        }
    }

    private static final class CreateGlossaryEntryCommandHandler
            extends AbstractWorldCommandHandler<CreateGlossaryEntryCommand> {

        CreateGlossaryEntryCommandHandler() {
            super(CreateGlossaryEntryCommand.class);
        }

        @Override
        public WorldState handle(WorldState current, CreateGlossaryEntryCommand command) {
            return current.addGlossaryEntry(new RuntimeGlossaryEntry(
                    command.glossaryId(), command.title(), command.sections()));
        }
    }
}
