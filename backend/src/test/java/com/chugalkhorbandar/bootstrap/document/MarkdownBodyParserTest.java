package com.chugalkhorbandar.bootstrap.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.document.model.DocumentSection;
import org.junit.jupiter.api.Test;

class MarkdownBodyParserTest {

    private final MarkdownBodyParser parser = new MarkdownBodyParser();

    @Test
    void parsesH1Heading() {
        String markdown =
                """
                ---
                id: doc
                title: Doc
                version: 1.0
                status: ACTIVE
                ---

                # Main Heading

                ## Section One
                text
                """;

        var body = parser.parse(markdown);

        assertThat(body.heading()).isEqualTo("Main Heading");
    }

    @Test
    void parsesOrderedSections() {
        String markdown =
                """
                # Title

                ## Alpha
                first

                ## Beta
                second

                ## Gamma
                third
                """;

        var body = parser.parse(markdown);

        assertThat(body.sections()).hasSize(3);
        assertThat(body.sections().get(0).title()).isEqualTo("Alpha");
        assertThat(body.sections().get(0).order()).isZero();
        assertThat(body.sections().get(1).title()).isEqualTo("Beta");
        assertThat(body.sections().get(1).order()).isEqualTo(1);
        assertThat(body.sections().get(2).title()).isEqualTo("Gamma");
        assertThat(body.sections().get(2).order()).isEqualTo(2);
    }

    @Test
    void preservesSectionContentAndOrder() {
        String markdown =
                """
                # Title

                ## One
                line a
                line b

                ## Two
                line c
                """;

        var body = parser.parse(markdown);

        assertThat(body.sections().get(0).content()).isEqualTo("line a\nline b");
        assertThat(body.sections().get(1).content()).isEqualTo("line c");
    }

    @Test
    void normalizesLineEndings() {
        String markdown = "# Title\r\n\r\n## Section\r\ncontent\r\n";

        var body = parser.parse(markdown);

        assertThat(body.heading()).isEqualTo("Title");
        assertThat(body.sections().getFirst().content()).isEqualTo("content");
    }

    @Test
    void treatsSubsequentH1LinesAsSections() {
        String markdown =
                """
                # Document Title

                # Legacy Section
                legacy content
                """;

        var body = parser.parse(markdown);

        assertThat(body.heading()).isEqualTo("Document Title");
        assertThat(body.sections()).hasSize(1);
        assertThat(body.sections().getFirst().title()).isEqualTo("Legacy Section");
    }
}
