package com.chugalkhorbandar.application.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SubjectTemplateRendererTest {

    @Test
    void replacesAvailablePlaceholders() {
        ReportTemplateContext context = new ReportTemplateContext(
                "2026-06-01",
                "Good morning.",
                "",
                "",
                "",
                "3",
                "2",
                "5",
                "1",
                "2",
                "4",
                "1",
                "0",
                "2",
                "Until tomorrow.",
                "",
                "",
                "");

        String subject = SubjectTemplateRenderer.render(
                "🐒 Bandar's Morning Letter — {date} ({conversationCount} conv, {promoted}/{discarded}/{pending})",
                context);

        assertThat(subject).isEqualTo("🐒 Bandar's Morning Letter — 2026-06-01 (3 conv, 1/2/1)");
    }

    @Test
    void leavesMissingPlaceholdersBlank() {
        ReportTemplateContext context = new ReportTemplateContext(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "");

        assertThat(SubjectTemplateRenderer.render("Report {date} {conversationCount}", context))
                .isEqualTo("Report  ");
    }
}
