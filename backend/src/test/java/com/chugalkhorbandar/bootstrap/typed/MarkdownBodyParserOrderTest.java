package com.chugalkhorbandar.bootstrap.typed;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.document.MarkdownBodyParser;
import com.chugalkhorbandar.bootstrap.document.model.DocumentBody;
import org.junit.jupiter.api.Test;

class MarkdownBodyParserOrderTest {

    private final MarkdownBodyParser parser = new MarkdownBodyParser();

    @Test
    void preservesSectionOrder() {
        String markdown =
                """
                # Title

                ## First
                one

                ## Second
                two

                ## Third
                three
                """;

        DocumentBody body = parser.parse(markdown);

        assertThat(body.sections()).extracting("title").containsExactly("First", "Second", "Third");
        assertThat(body.sections()).extracting("order").containsExactly(0, 1, 2);
    }

    @Test
    void detectsDuplicateSectionTitlesForValidation() {
        String markdown =
                """
                # Title

                ## Alpha
                a

                ## Alpha
                b
                """;

        assertThat(parser.sectionTitles(markdown)).containsExactly("Alpha", "Alpha");
    }
}
