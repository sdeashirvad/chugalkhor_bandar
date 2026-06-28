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
public class DuplicateFilenameValidator implements ValidationRule {

    @Override
    public List<ValidationIssue> validate(BootstrapWorld world) {
        Map<String, BootstrapFile> seen = new HashMap<>();
        List<ValidationIssue> issues = new ArrayList<>();

        for (BootstrapFile file : world.markdownFiles()) {
            if (file.category() == BootstrapFileCategory.EXCLUDED) {
                continue;
            }
            String filename = file.filePath().getFileName().toString();
            String scopeKey = file.filePath().getParent() + "/" + filename;
            BootstrapFile previous = seen.put(scopeKey, file);
            if (previous != null) {
                issues.add(new ValidationIssue(
                        ValidationSeverity.ERROR,
                        "Duplicate filename '" + filename + "' in " + file.filePath().getParent(),
                        Optional.of(file.filePath())));
            }
        }
        return issues;
    }
}
