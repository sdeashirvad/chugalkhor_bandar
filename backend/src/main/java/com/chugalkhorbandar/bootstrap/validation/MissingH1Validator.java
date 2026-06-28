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
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MissingH1Validator implements ValidationRule {

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
                if (bodyParser.countH1Headings(markdown) == 0) {
                    issues.add(new ValidationIssue(
                            ValidationSeverity.ERROR,
                            "Document must contain an H1 heading",
                            Optional.of(file.filePath())));
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to read file: " + file.filePath(), e);
            }
        }

        return issues;
    }
}
