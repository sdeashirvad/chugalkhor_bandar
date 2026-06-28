package com.chugalkhorbandar.bootstrap.compiler.command;

import java.nio.file.Path;
import java.util.Map;

public record CreateCharacterCommand(
        String commandId,
        int executionOrder,
        String sourceDocumentId,
        Path sourcePath,
        String characterId,
        String title,
        Map<String, String> sections,
        String currentPlaceId,
        String homeTerritoryId,
        Map<String, String> metadata)
        implements BootstrapCommand {

    @Override
    public String commandType() {
        return "CreateCharacter";
    }
}
