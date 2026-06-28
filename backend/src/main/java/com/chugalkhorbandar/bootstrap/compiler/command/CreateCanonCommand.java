package com.chugalkhorbandar.bootstrap.compiler.command;

import java.nio.file.Path;
import java.util.Map;

public record CreateCanonCommand(
        String commandId,
        int executionOrder,
        String sourceDocumentId,
        Path sourcePath,
        String canonId,
        String title,
        Map<String, String> sections,
        Map<String, String> metadata)
        implements BootstrapCommand {

    @Override
    public String commandType() {
        return "CreateCanon";
    }
}
