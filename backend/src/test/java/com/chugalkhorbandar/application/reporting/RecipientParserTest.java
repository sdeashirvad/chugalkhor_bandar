package com.chugalkhorbandar.application.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RecipientParserTest {

    @Test
    void parsesCommaSeparatedRecipients() {
        assertThat(RecipientParser.parse("a@gmail.com,b@gmail.com,c@gmail.com"))
                .containsExactly("a@gmail.com", "b@gmail.com", "c@gmail.com");
    }

    @Test
    void parsesSemicolonSeparatedRecipients() {
        assertThat(RecipientParser.parse("a@gmail.com;b@gmail.com"))
                .containsExactly("a@gmail.com", "b@gmail.com");
    }

    @Test
    void ignoresWhitespaceAndInvalidEmails() {
        assertThat(RecipientParser.parse(" a@gmail.com , not-an-email , b@gmail.com "))
                .containsExactly("a@gmail.com", "b@gmail.com");
    }

    @Test
    void returnsEmptyForBlankInput() {
        assertThat(RecipientParser.parse("")).isEmpty();
        assertThat(RecipientParser.parse(null)).isEmpty();
    }
}
