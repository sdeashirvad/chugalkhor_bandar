package com.chugalkhorbandar.bootstrap.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.scanner.BootstrapScanner;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InvalidStatusValidatorTest {

    private final BootstrapScanner scanner =
            new BootstrapScanner(new ManifestParser(), new FrontmatterParser());
    private final InvalidStatusValidator validator = new InvalidStatusValidator();

    @Test
    void rejectsInvalidStatusValues(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/bad-status.md"),
                """
                ---
                id: character_bad
                name: Bad Status
                version: 1.0
                status: Alive
                ---

                # Bad
                """);

        var world = scanner.scan(tempDir);
        var issues = validator.validate(world);

        assertThat(issues).anyMatch(issue -> issue.message().contains("Invalid status"));
    }
}
