package com.chugalkhorbandar.domain.world.commands;

import java.time.Instant;

public sealed interface WorldCommand
        permits CreateCharacterCommand,
                UpdateCharacterCommand,
                DeleteCharacterCommand,
                CreateTerritoryCommand,
                TransferTerritoryCommand,
                ChangeTerritoryRulerCommand,
                CreatePlaceCommand,
                MoveCharacterCommand,
                CreateStoryCommand,
                LinkStoryCommand,
                CreateRelationshipCommand,
                RemoveRelationshipCommand,
                ChangePreferenceCommand,
                CreateObjectCommand,
                TransferObjectCommand,
                ConsumeResourceCommand,
                CreateOrganizationCommand,
                AssignOrganizationRoleCommand,
                RecordTimelineEntryCommand,
                CreatePromptProfileCommand,
                CreateLawCommand,
                CreateCustomCommand,
                CreateGlossaryEntryCommand {

    String commandId();

    String correlationId();

    Instant createdAt();

    CommandSource source();

    String initiatedBy();

    String reason();

    CommandMetadata metadata();

    String commandType();
}
