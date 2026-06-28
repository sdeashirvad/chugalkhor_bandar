package com.chugalkhorbandar.bootstrap.model;

import java.nio.file.Path;
import java.util.Optional;

public record BootstrapFile(
        Path filePath, BootstrapFileCategory category, Optional<DocumentMetadata> metadata) {}
