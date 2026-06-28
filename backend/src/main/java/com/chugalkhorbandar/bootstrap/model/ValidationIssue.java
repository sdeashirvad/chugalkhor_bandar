package com.chugalkhorbandar.bootstrap.model;

import java.nio.file.Path;
import java.util.Optional;

public record ValidationIssue(
        ValidationSeverity severity, String message, Optional<Path> filePath) {}
