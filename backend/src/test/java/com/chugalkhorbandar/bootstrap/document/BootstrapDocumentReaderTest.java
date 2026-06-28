package com.chugalkhorbandar.bootstrap.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.scanner.BootstrapScanner;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BootstrapDocumentReaderTest {

    private final BootstrapDocumentReader reader = new BootstrapDocumentReader();
    private final BootstrapScanner scanner =
            new BootstrapScanner(new ManifestParser(), new FrontmatterParser());

    @Test
    void loadsDocumentWithMetadataAndBody(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        Path file = tempDir.resolve("characters/hero.md");

        var document = reader.read(tempDir, file);

        assertThat(document.metadata().id()).isEqualTo("character_hero");
        assertThat(document.heading()).isEqualTo("Hero");
        assertThat(document.getSections()).hasSize(1);
        assertThat(document.hasSection("Summary")).isTrue();
        assertThat(document.sourcePath()).isEqualTo(file);
        assertThat(document.documentType()).isEqualTo(DocumentType.CHARACTER);
        assertThat(document.originalMarkdown()).contains("id: character_hero");
    }

    @Test
    void supportsSectionLookup(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("sample.md"),
                """
                ---
                id: sample_doc
                title: Sample
                version: 1.0
                status: ACTIVE
                ---

                # Sample

                ## Details
                detail text
                """);

        var document = reader.read(tempDir, tempDir.resolve("sample.md"));

        assertThat(document.hasSection("details")).isTrue();
        assertThat(document.getContent("Details")).isEqualTo("detail text");
        assertThat(document.findSection("DETAILS")).isPresent();
    }
}
