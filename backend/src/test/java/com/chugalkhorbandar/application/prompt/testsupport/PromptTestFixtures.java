package com.chugalkhorbandar.application.prompt.testsupport;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptComposer;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import java.util.List;

public final class PromptTestFixtures {

    private PromptTestFixtures() {}

    public static ComposedPrompt sceneEstablishmentPrompt() {
        return new ComposedPrompt(List.of(
                fragmentSection(KnowledgeFragmentType.IDENTITY, 1, "I am Bandar, the oldest living being in the Jungle."),
                PromptSection.of(
                        PromptSectionType.CURRENT_USER,
                        "Current User",
                        true,
                        """
                        Name: Hippu King
                        Titles: King
                        Species: Hippu
                        Home: Hippu Kingdom"""),
                fragmentSection(
                        KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR,
                        10,
                        "Bandar respects Hippu King for balancing strength with kindness."),
                fragmentSection(KnowledgeFragmentType.SPEAKING_STYLE, 15, "Speak warmly and simply."),
                PromptSection.of(PromptSectionType.WORLD_FACTS, "World Facts", true, "The Jungle exists."),
                PromptSection.of(PromptSectionType.CURRENT_CONVERSATION, "Current Conversation", true, "No active conversation."),
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Who am I?"),
                PromptSection.of(PromptSectionType.INSTRUCTIONS, "Instructions", true, PromptComposer.DEFAULT_INSTRUCTION)));
    }

    private static PromptSection fragmentSection(KnowledgeFragmentType type, int priority, String content) {
        PromptSectionType sectionType =
                type == KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR
                        ? PromptSectionType.RELATIONSHIP_TO_BANDAR
                        : PromptSectionType.PERSONALITY;
        return PromptSection.fromFragment(
                sectionType, type.name(), priority, true, content.length() / 4, content, "test:" + type, type);
    }
}
