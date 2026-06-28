package com.chugalkhorbandar.bootstrap.model;

import java.nio.file.Path;

public record Manifest(
        String worldId,
        String worldName,
        String bootstrapVersion,
        String schemaVersion,
        String createdBy,
        String createdAt,
        String language,
        Path filePath) {}
