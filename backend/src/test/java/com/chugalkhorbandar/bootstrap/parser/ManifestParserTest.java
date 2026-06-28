package com.chugalkhorbandar.bootstrap.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ManifestParserTest {

    private final ManifestParser parser = new ManifestParser();

    @Test
    void parsesManifestWithRequiredFields(@TempDir Path tempDir) throws Exception {
        Path manifestPath = tempDir.resolve("manifest.yaml");
        Files.writeString(
                manifestPath,
                """
                worldId: test_world
                worldName: Test World
                bootstrapVersion: "1.0"
                schemaVersion: "1"
                createdBy: tester
                createdAt: "2026-01-01"
                language: en
                """);

        var manifest = parser.parse(manifestPath);

        assertThat(manifest.worldId()).isEqualTo("test_world");
        assertThat(manifest.worldName()).isEqualTo("Test World");
        assertThat(manifest.bootstrapVersion()).isEqualTo("1.0");
        assertThat(manifest.schemaVersion()).isEqualTo("1");
        assertThat(manifest.createdBy()).isEqualTo("tester");
        assertThat(manifest.createdAt()).isEqualTo("2026-01-01");
        assertThat(manifest.language()).isEqualTo("en");
        assertThat(manifest.filePath()).isEqualTo(manifestPath);
    }
}
