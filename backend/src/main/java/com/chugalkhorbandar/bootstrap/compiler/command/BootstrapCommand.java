package com.chugalkhorbandar.bootstrap.compiler.command;

import java.nio.file.Path;

public sealed interface BootstrapCommand
        permits CreateCanonCommand,
                CreateWorldRulesCommand,
                CreatePromptProfileCommand,
                CreateTerritoryCommand,
                CreatePlaceCommand,
                CreateOrganizationCommand,
                CreateResourceCommand,
                CreateObjectCommand,
                CreateCharacterCommand,
                CreateRelationshipCommand,
                CreateStoryCommand,
                CreateChronologyCommand,
                CreateLawCommand,
                CreateCustomCommand,
                CreateGlossaryEntryCommand {

    String commandId();

    int executionOrder();

    String sourceDocumentId();

    Path sourcePath();

    String commandType();
}
