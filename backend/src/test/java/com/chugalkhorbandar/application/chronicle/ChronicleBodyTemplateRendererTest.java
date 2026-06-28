package com.chugalkhorbandar.application.chronicle;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChronicleBodyTemplateRendererTest {

    @Test
    void rendersPromiseNarrative() {
        String body = ChronicleBodyTemplateRenderer.render(
                ChronicleCategory.PROMISE, "Bandar", "Hippu King", "one day he would tell the story of the Lost Crown");

        assertThat(body).isEqualTo(
                "Bandar promised Hippu King that one day he would tell the story of the Lost Crown.");
    }

    @Test
    void deterministicTemplateName() {
        assertThat(ChronicleBodyTemplateRenderer.templateName(ChronicleCategory.STORY)).isEqualTo("story-narrative-v1");
    }
}
