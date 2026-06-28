package com.chugalkhorbandar.bootstrap.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationSeverity;
import com.chugalkhorbandar.bootstrap.scanner.BootstrapScanner;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DuplicateIdValidatorTest {

    private final BootstrapScanner scanner =
            new BootstrapScanner(new ManifestParser(), new FrontmatterParser());
    private final DuplicateIdValidator validator = new DuplicateIdValidator();

    @Test
    void detectsDuplicateIds(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/villain.md"),
                """
                ---
                id: character_hero
                name: Villain
                version: 1.0
                status: ACTIVE
                ---

                # Villain
                """);

        BootstrapWorld world = scanner.scan(tempDir);
        var issues = validator.validate(world);

        assertThat(issues).hasSize(1);
        assertThat(issues.getFirst().severity()).isEqualTo(ValidationSeverity.ERROR);
        assertThat(issues.getFirst().message()).contains("Duplicate id");
    }
}
