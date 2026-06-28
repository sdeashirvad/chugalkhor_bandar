package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.model.BootstrapFile;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.DocumentMetadata;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class StoryValidator implements ValidationRule {

    @Override
    public List<ValidationIssue> validate(BootstrapWorld world) {
        List<ValidationIssue> issues = new ArrayList<>();
        for (BootstrapFile file : world.stories()) {
            if (file.metadata().isEmpty()) {
                continue;
            }
            validateStoryMetadata(file.metadata().get(), issues);
        }
        return issues;
    }

    private void validateStoryMetadata(DocumentMetadata metadata, List<ValidationIssue> issues) {
        if (metadata.id() == null || metadata.id().isBlank()) {
            issues.add(error("Story missing required field: id", metadata.filePath()));
        } else if (!metadata.id().startsWith("story_")) {
            issues.add(error("Story id must start with 'story_'", metadata.filePath()));
        }
        if (metadata.title() == null || metadata.title().isBlank()) {
            issues.add(error("Story missing required field: title", metadata.filePath()));
        }
        if (metadata.version() == null || metadata.version().isBlank()) {
            issues.add(error("Story missing required field: version", metadata.filePath()));
        }
        if (metadata.status() == null || metadata.status().isBlank()) {
            issues.add(error("Story missing required field: status", metadata.filePath()));
        }
    }

    private ValidationIssue error(String message, java.nio.file.Path filePath) {
        return new ValidationIssue(ValidationSeverity.ERROR, message, Optional.of(filePath));
    }
}
