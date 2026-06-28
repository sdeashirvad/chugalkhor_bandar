package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.model.BootstrapFile;
import com.chugalkhorbandar.bootstrap.model.BootstrapFileCategory;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DuplicateIdValidator implements ValidationRule {

    @Override
    public List<ValidationIssue> validate(BootstrapWorld world) {
        Map<String, BootstrapFile> seen = new HashMap<>();
        List<ValidationIssue> issues = new ArrayList<>();

        for (BootstrapFile file : world.markdownFiles()) {
            if (file.category() == BootstrapFileCategory.EXCLUDED || file.metadata().isEmpty()) {
                continue;
            }
            String id = file.metadata().get().id();
            if (id == null || id.isBlank()) {
                continue;
            }
            BootstrapFile previous = seen.put(id, file);
            if (previous != null) {
                issues.add(new ValidationIssue(
                        ValidationSeverity.ERROR,
                        "Duplicate id '" + id + "' also used in " + previous.filePath().getFileName(),
                        Optional.of(file.filePath())));
            }
        }
        return issues;
    }
}
