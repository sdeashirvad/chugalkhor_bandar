package com.chugalkhorbandar.application.prompt;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.llm.PromptToProviderAdapter;
import com.chugalkhorbandar.application.llm.ProviderMessage;
import com.chugalkhorbandar.application.prompt.testsupport.PromptTestFixtures;
import com.chugalkhorbandar.config.LlmProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Prompt23_5ARegressionTest {

    private PromptToProviderAdapter adapter;

    @BeforeEach
    void setUp() {
        LlmProperties properties = new LlmProperties();
        properties.setModel("mock-bandar");
        adapter = new PromptToProviderAdapter(properties);
    }

    @Test
    void llmPromptUsesNaturalInWorldHeadings() {
        List<ProviderMessage> messages = adapter.adapt(PromptTestFixtures.sceneEstablishmentPrompt()).messages();

        String combined = messages.stream().map(ProviderMessage::content).reduce("", String::concat);

        assertThat(combined).contains("Your Identity");
        assertThat(combined).contains("The Current Speaker");
        assertThat(combined).contains("Your Relationship");
        assertThat(combined).contains("Your Speaking Style");
        assertThat(combined).contains("Relevant Knowledge");
        assertThat(combined).contains("Current Conversation");
        assertThat(combined).contains("Instructions");
        assertThat(combined).doesNotContain("Current User");
        assertThat(combined).doesNotContain("System Identity");
        assertThat(combined).doesNotContain("Personality Fragment");
        assertThat(combined).doesNotContain("CURRENT_USER");
    }

    @Test
    void identitySectionLeadsWithYouAreBandar() {
        String identity = LlmPromptPresenter.format(fragmentSection());

        assertThat(identity).startsWith("Your Identity");
        assertThat(identity.indexOf("You are Bandar.")).isLessThan(identity.indexOf("oldest living being"));
    }

    @Test
    void identitySectionForbidsThirdPersonBandar() {
        String identity = LlmPromptPresenter.body(fragmentSection());

        assertThat(identity).contains("Never refer to Bandar as someone separate from yourself");
        assertThat(identity).contains("Never describe Bandar as another character");
    }

    @Test
    void instructionsForbidFindingBandar() {
        PromptSection instructions =
                PromptSection.of(PromptSectionType.INSTRUCTIONS, "Instructions", true, PromptComposer.DEFAULT_INSTRUCTION);

        String body = LlmPromptPresenter.body(instructions);

        assertThat(body).contains("Never send the speaker to find Bandar");
        assertThat(body).contains("Always speak about yourself in the first person");
    }

    @Test
    void sceneEstablishmentOrderPlacesIdentityBeforeKnowledge() {
        List<ProviderMessage> messages = adapter.adapt(PromptTestFixtures.sceneEstablishmentPrompt()).messages();
        List<String> systemMessages = messages.stream()
                .filter(message -> message.role().name().equals("SYSTEM"))
                .map(ProviderMessage::content)
                .toList();

        assertThat(indexOfHeading(systemMessages, "Your Identity"))
                .isLessThan(indexOfHeading(systemMessages, "The Current Speaker"));
        assertThat(indexOfHeading(systemMessages, "The Current Speaker"))
                .isLessThan(indexOfHeading(systemMessages, "Your Relationship"));
        assertThat(indexOfHeading(systemMessages, "Your Relationship"))
                .isLessThan(indexOfHeading(systemMessages, "Your Speaking Style"));
        assertThat(indexOfHeading(systemMessages, "Your Speaking Style"))
                .isLessThan(indexOfHeading(systemMessages, "Relevant Knowledge"));
        assertThat(indexOfHeading(systemMessages, "Relevant Knowledge"))
                .isLessThan(indexOfHeading(systemMessages, "Instructions"));
    }

    @Test
    void relationshipSectionEncouragesNaturalTone() {
        PromptSection relationship = PromptSection.fromFragment(
                PromptSectionType.RELATIONSHIP_TO_BANDAR,
                "Relationship to Bandar",
                10,
                false,
                20,
                "Bandar respects Hippu King.",
                "rel",
                com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR);

        assertThat(LlmPromptPresenter.body(relationship)).contains("naturally shape your tone");
    }

    private static PromptSection fragmentSection() {
        return PromptSection.fromFragment(
                PromptSectionType.PERSONALITY,
                "Bandar Identity",
                1,
                true,
                10,
                "I am Bandar, the oldest living being in the Jungle.",
                "identity",
                com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType.IDENTITY);
    }

    private static int indexOfHeading(List<String> messages, String heading) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).startsWith(heading)) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }
}
