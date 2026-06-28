package com.chugalkhorbandar.application.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ClosingSelectorTest {

    @Test
    void selectsDeterministicallyFromReportId() {
        List<String> closings = List.of("One.", "Two.", "Three.", "Four.");
        String first = ClosingSelector.select("run-abc", closings);
        String second = ClosingSelector.select("run-abc", closings);

        assertThat(first).isEqualTo(second);
        assertThat(closings).contains(first);
    }

    @Test
    void usesDefaultWhenClosingsEmpty() {
        assertThat(ClosingSelector.select("run-1", List.of())).isEqualTo("Until tomorrow.");
    }
}
