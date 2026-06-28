package com.chugalkhorbandar.application.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FriendlySenderParserTest {

    @Test
    void parsesFriendlySenderFormat() {
        assertThat(FriendlySenderParser.parse("Bandar <bandar@ashirvad.work>"))
                .isEqualTo("Bandar <bandar@ashirvad.work>");
    }

    @Test
    void trimsWhitespaceAroundFriendlySender() {
        assertThat(FriendlySenderParser.parse("  Bandar <bandar@ashirvad.work>  "))
                .isEqualTo("Bandar <bandar@ashirvad.work>");
    }

    @Test
    void returnsPlainEmailWhenNotFriendlyFormat() {
        assertThat(FriendlySenderParser.parse("bandar@ashirvad.work")).isEqualTo("bandar@ashirvad.work");
    }

    @Test
    void returnsEmptyForBlankInput() {
        assertThat(FriendlySenderParser.parse("")).isEmpty();
        assertThat(FriendlySenderParser.parse(null)).isEmpty();
    }
}
