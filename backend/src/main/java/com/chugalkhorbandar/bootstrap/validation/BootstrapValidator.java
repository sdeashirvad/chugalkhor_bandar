package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import com.chugalkhorbandar.bootstrap.model.ValidationReport;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import com.chugalkhorbandar.bootstrap.model.ValidationStatus;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BootstrapValidator {

    private final List<ValidationRule> rules;

    public BootstrapValidator(
            ManifestValidator manifestValidator,
            MissingFrontmatterValidator missingFrontmatterValidator,
            RequiredFieldValidator requiredFieldValidator,
            EmptyIdValidator emptyIdValidator,
            InvalidStatusValidator invalidStatusValidator,
            DuplicateIdValidator duplicateIdValidator,
            DuplicateFilenameValidator duplicateFilenameValidator,
            CharacterValidator characterValidator,
            StoryValidator storyValidator,
            ChronologyValidator chronologyValidator,
            PromptValidator promptValidator,
            MissingH1Validator missingH1Validator,
            DuplicateSectionValidator duplicateSectionValidator) {
        this.rules = List.of(
                manifestValidator,
                missingFrontmatterValidator,
                requiredFieldValidator,
                emptyIdValidator,
                invalidStatusValidator,
                duplicateIdValidator,
                duplicateFilenameValidator,
                characterValidator,
                storyValidator,
                chronologyValidator,
                promptValidator,
                missingH1Validator,
                duplicateSectionValidator);
    }

    public ValidationReport validate(BootstrapWorld world) {
        List<ValidationIssue> issues = new ArrayList<>();
        for (ValidationRule rule : rules) {
            issues.addAll(rule.validate(world));
        }

        int errorCount = (int) issues.stream()
                .filter(issue -> issue.severity() == ValidationSeverity.ERROR)
                .count();
        int warningCount = (int) issues.stream()
                .filter(issue -> issue.severity() == ValidationSeverity.WARNING)
                .count();

        boolean manifestValid = world.manifest().isPresent()
                && issues.stream()
                        .noneMatch(issue -> issue.severity() == ValidationSeverity.ERROR
                                && issue.message().toLowerCase().contains("manifest"));

        ValidationStatus status = errorCount == 0 ? ValidationStatus.VALID : ValidationStatus.INVALID;

        return new ValidationReport(
                manifestValid,
                world.characters().size(),
                world.stories().size(),
                world.prompts().size(),
                world.chronology().size(),
                warningCount,
                errorCount,
                status,
                List.copyOf(issues));
    }
}
