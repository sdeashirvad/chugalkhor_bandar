package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.Manifest;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ManifestValidator implements ValidationRule {

    @Override
    public List<ValidationIssue> validate(BootstrapWorld world) {
        List<ValidationIssue> issues = new ArrayList<>();

        if (world.manifest().isEmpty()) {
            issues.add(issue("Manifest file (manifest.yaml) is missing", world.rootPath().resolve("manifest.yaml")));
            return issues;
        }

        Manifest manifest = world.manifest().get();
        validateField(manifest.worldId(), "worldId", manifest.filePath(), issues);
        validateField(manifest.worldName(), "worldName", manifest.filePath(), issues);
        validateField(manifest.bootstrapVersion(), "bootstrapVersion", manifest.filePath(), issues);
        validateField(manifest.schemaVersion(), "schemaVersion", manifest.filePath(), issues);
        validateField(manifest.createdBy(), "createdBy", manifest.filePath(), issues);
        validateField(manifest.createdAt(), "createdAt", manifest.filePath(), issues);
        validateField(manifest.language(), "language", manifest.filePath(), issues);

        return issues;
    }

    private void validateField(String value, String fieldName, java.nio.file.Path filePath, List<ValidationIssue> issues) {
        if (value == null || value.isBlank()) {
            issues.add(issue("Manifest missing required field: " + fieldName, filePath));
        }
    }

    private ValidationIssue issue(String message, java.nio.file.Path filePath) {
        return new ValidationIssue(ValidationSeverity.ERROR, message, Optional.of(filePath));
    }
}
