package com.chugalkhorbandar.bootstrap.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.model.ValidationStatus;
import com.chugalkhorbandar.bootstrap.scanner.BootstrapScanner;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ValidationReportTest {

    private final BootstrapScanner scanner =
            new BootstrapScanner(new ManifestParser(), new FrontmatterParser());

    private final BootstrapValidator validator = new BootstrapValidator(
            new ManifestValidator(),
            new MissingFrontmatterValidator(),
            new RequiredFieldValidator(),
            new EmptyIdValidator(),
            new InvalidStatusValidator(),
            new DuplicateIdValidator(),
            new DuplicateFilenameValidator(),
            new CharacterValidator(),
            new StoryValidator(),
            new ChronologyValidator(),
            new PromptValidator(),
            new MissingH1Validator(),
            new DuplicateSectionValidator());

    @Test
    void producesValidReportForValidBootstrap(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);

        var world = scanner.scan(tempDir);
        var report = validator.validate(world);

        assertThat(report.isValid()).isTrue();
        assertThat(report.status()).isEqualTo(ValidationStatus.VALID);
        assertThat(report.errorCount()).isZero();
        assertThat(report.characterCount()).isEqualTo(1);
        assertThat(report.storyCount()).isEqualTo(1);
        assertThat(report.toSummary()).contains("Status\nVALID");
    }

    @Test
    void producesInvalidReportWhenManifestMissing(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        java.nio.file.Files.delete(tempDir.resolve("manifest.yaml"));

        var world = scanner.scan(tempDir);
        var report = validator.validate(world);

        assertThat(report.isValid()).isFalse();
        assertThat(report.status()).isEqualTo(ValidationStatus.INVALID);
        assertThat(report.errorCount()).isGreaterThan(0);
        assertThat(report.toSummary()).contains("Status\nINVALID");
    }
}
