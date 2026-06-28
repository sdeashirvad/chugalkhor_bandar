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

class MissingH1ValidatorTest {

    private final BootstrapScanner scanner =
            new BootstrapScanner(new ManifestParser(), new FrontmatterParser());
    private final MissingH1Validator validator = new MissingH1Validator();

    @Test
    void rejectsDocumentWithoutH1(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/no-heading.md"),
                """
                ---
                id: character_no_heading
                name: No Heading
                version: 1.0
                status: ACTIVE
                ---

                Plain text without a heading.
                """);

        var world = scanner.scan(tempDir);
        var issues = validator.validate(world);

        assertThat(issues).anyMatch(issue -> issue.severity() == ValidationSeverity.ERROR
                && issue.message().contains("H1 heading"));
    }
}
