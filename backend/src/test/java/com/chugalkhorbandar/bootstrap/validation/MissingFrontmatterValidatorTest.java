package com.chugalkhorbandar.bootstrap.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import com.chugalkhorbandar.bootstrap.scanner.BootstrapScanner;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MissingFrontmatterValidatorTest {

    private final BootstrapScanner scanner =
            new BootstrapScanner(new ManifestParser(), new FrontmatterParser());
    private final MissingFrontmatterValidator validator = new MissingFrontmatterValidator();

    @Test
    void rejectsFilesWithoutFrontmatter(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/no-frontmatter.md"), "# Missing frontmatter");

        var world = scanner.scan(tempDir);
        var issues = validator.validate(world);

        assertThat(issues).anyMatch(issue -> issue.severity() == ValidationSeverity.ERROR
                && issue.message().contains("missing YAML frontmatter"));
    }
}
