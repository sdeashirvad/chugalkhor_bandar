package com.chugalkhorbandar.application.prompt;

import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;

public record PromptSection(
        PromptSectionType sectionType,
        String title,
        int priority,
        boolean required,
        int estimatedTokens,
        String content,
        String fragmentId,
        KnowledgeFragmentType fragmentType) {

    public PromptSection {
        fragmentId = fragmentId == null ? "" : fragmentId;
        fragmentType = fragmentType == null ? KnowledgeFragmentType.UNKNOWN : fragmentType;
    }

    public static PromptSection of(
            PromptSectionType sectionType, String title, boolean required, String content) {
        return new PromptSection(
                sectionType,
                title,
                PromptSectionPriorities.priority(sectionType),
                required,
                ContextSection.estimateTokensFromContent(content),
                content,
                "",
                KnowledgeFragmentType.UNKNOWN);
    }

    public static PromptSection fromFragment(
            PromptSectionType sectionType,
            String title,
            int priority,
            boolean required,
            int estimatedTokens,
            String content,
            String fragmentId,
            KnowledgeFragmentType fragmentType) {
        return new PromptSection(
                sectionType, title, priority, required, estimatedTokens, content, fragmentId, fragmentType);
    }

    public boolean isFragment() {
        return fragmentId != null && !fragmentId.isBlank();
    }
}
