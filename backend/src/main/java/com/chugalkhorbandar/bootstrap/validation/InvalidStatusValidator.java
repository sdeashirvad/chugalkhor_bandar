package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.model.BootstrapFile;
import com.chugalkhorbandar.bootstrap.model.BootstrapFileCategory;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class InvalidStatusValidator implements ValidationRule {

    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "DRAFT", "DEPRECATED", "ARCHIVED");

    @Override
    public List<ValidationIssue> validate(BootstrapWorld world) {
        List<ValidationIssue> issues = new ArrayList<>();
        for (BootstrapFile file : world.markdownFiles()) {
            if (file.category() == BootstrapFileCategory.EXCLUDED || file.metadata().isEmpty()) {
                continue;
            }
            String status = file.metadata().get().status();
            if (status != null && !status.isBlank() && !ALLOWED_STATUSES.contains(status)) {
                issues.add(new ValidationIssue(
                        ValidationSeverity.ERROR,
                        "Invalid status '" + status + "'. Allowed values: ACTIVE, DRAFT, DEPRECATED, ARCHIVED",
                        Optional.of(file.filePath())));
            }
        }
        return issues;
    }
}
