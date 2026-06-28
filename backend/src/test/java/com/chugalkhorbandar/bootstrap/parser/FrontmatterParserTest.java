package com.chugalkhorbandar.bootstrap.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.model.DocumentMetadata;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FrontmatterParserTest {

    private final FrontmatterParser parser = new FrontmatterParser();

    @Test
    void parsesFrontmatterFields(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("doc.md");
        Files.writeString(
                file,
                """
                ---
                id: doc_one
                title: Document One
                version: 1.0
                status: ACTIVE
                ---

                # Body
                """);

        var frontmatter = parser.parseFrontmatter(file);

        assertThat(frontmatter).isPresent();
        DocumentMetadata metadata = DocumentMetadata.fromFrontmatter(frontmatter.get(), file);
        assertThat(metadata.id()).isEqualTo("doc_one");
        assertThat(metadata.title()).isEqualTo("Document One");
        assertThat(metadata.version()).isEqualTo("1.0");
        assertThat(metadata.status()).isEqualTo("ACTIVE");
        assertThat(metadata.filePath()).isEqualTo(file);
    }

    @Test
    void usesNameAsTitleFallback(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("character.md");
        Files.writeString(
                file,
                """
                ---
                id: character_hero
                name: Hero
                version: 1.0
                status: ACTIVE
                ---

                # Hero
                """);

        var frontmatter = parser.parseFrontmatter(file);
        DocumentMetadata metadata = DocumentMetadata.fromFrontmatter(frontmatter.get(), file);

        assertThat(metadata.title()).isEqualTo("Hero");
    }

    @Test
    void returnsEmptyWhenFrontmatterMissing(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("plain.md");
        Files.writeString(file, "# No frontmatter");

        assertThat(parser.parseFrontmatter(file)).isEmpty();
    }
}
