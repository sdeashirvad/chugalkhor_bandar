package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.model.BootstrapFile;
import com.chugalkhorbandar.bootstrap.model.BootstrapFileCategory;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.DocumentMetadata;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RequiredFieldValidator implements ValidationRule {

    private static final List<String> REQUIRED_FIELDS = List.of("id", "title", "version", "status");

    @Override
    public List<ValidationIssue> validate(BootstrapWorld world) {
        List<ValidationIssue> issues = new ArrayList<>();
        for (BootstrapFile file : world.markdownFiles()) {
            if (file.category() == BootstrapFileCategory.EXCLUDED || file.metadata().isEmpty()) {
                continue;
            }
            DocumentMetadata metadata = file.metadata().get();
            for (String field : REQUIRED_FIELDS) {
                if (isMissing(metadata, field)) {
                    issues.add(new ValidationIssue(
                            ValidationSeverity.ERROR,
                            "Missing required frontmatter field: " + field,
                            Optional.of(file.filePath())));
                }
            }
        }
        return issues;
    }

    private boolean isMissing(DocumentMetadata metadata, String field) {
        return switch (field) {
            case "id" -> metadata.id() == null || metadata.id().isBlank();
            case "title" -> metadata.title() == null || metadata.title().isBlank();
            case "version" -> metadata.version() == null || metadata.version().isBlank();
            case "status" -> metadata.status() == null || metadata.status().isBlank();
            default -> false;
        };
    }
}
