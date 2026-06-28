package com.chugalkhorbandar.bootstrap.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public record BootstrapWorld(
        Path rootPath,
        Optional<Manifest> manifest,
        List<BootstrapFile> characters,
        List<BootstrapFile> stories,
        List<BootstrapFile> prompts,
        List<BootstrapFile> chronology,
        List<BootstrapFile> references,
        List<BootstrapFile> allValidatedFiles) {

    public List<BootstrapFile> markdownFiles() {
        return allValidatedFiles;
    }
}
