package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.model.BootstrapFile;
import com.chugalkhorbandar.bootstrap.model.BootstrapFileCategory;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EmptyIdValidator implements ValidationRule {

    @Override
    public List<ValidationIssue> validate(BootstrapWorld world) {
        List<ValidationIssue> issues = new ArrayList<>();
        for (BootstrapFile file : world.markdownFiles()) {
            if (file.category() == BootstrapFileCategory.EXCLUDED || file.metadata().isEmpty()) {
                continue;
            }
            String id = file.metadata().get().id();
            if (id != null && id.isBlank()) {
                issues.add(new ValidationIssue(
                        ValidationSeverity.ERROR,
                        "Document id must not be empty",
                        Optional.of(file.filePath())));
            }
        }
        return issues;
    }
}
