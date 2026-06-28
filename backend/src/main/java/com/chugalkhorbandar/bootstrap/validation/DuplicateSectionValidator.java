package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.document.MarkdownBodyParser;
import com.chugalkhorbandar.bootstrap.model.BootstrapFile;
import com.chugalkhorbandar.bootstrap.model.BootstrapFileCategory;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class DuplicateSectionValidator implements ValidationRule {

    private final MarkdownBodyParser bodyParser = new MarkdownBodyParser();

    @Override
    public List<ValidationIssue> validate(BootstrapWorld world) {
        List<ValidationIssue> issues = new ArrayList<>();

        for (BootstrapFile file : world.markdownFiles()) {
            if (file.category() == BootstrapFileCategory.EXCLUDED) {
                continue;
            }
            try {
                String markdown = Files.readString(file.filePath());
                Set<String> seen = new HashSet<>();
                for (String title : bodyParser.sectionTitles(markdown)) {
                    if (title.isBlank()) {
                        continue;
                    }
                    String key = title.toLowerCase(Locale.ROOT);
                    if (!seen.add(key)) {
                        issues.add(new ValidationIssue(
                                ValidationSeverity.WARNING,
                                "Duplicate section heading: " + title,
                                Optional.of(file.filePath())));
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read file: " + file.filePath(), e);
            }
        }

        return issues;
    }
}
