package com.chugalkhorbandar.application.context.knowledge;

import com.chugalkhorbandar.application.context.ContextSection;
import java.util.List;
import java.util.Set;

public record KnowledgeFragment(
        String fragmentId,
        KnowledgeFragmentType fragmentType,
        String title,
        String content,
        String sourceDocument,
        String sourceSection,
        int estimatedTokens,
        Set<String> tags,
        double confidence) {

    public KnowledgeFragment {
        tags = tags == null ? Set.of() : Set.copyOf(tags);
    }

    public static KnowledgeFragment of(
            KnowledgeFragmentType fragmentType,
            String title,
            String content,
            String sourceDocument,
            String sourceSection,
            Set<String> tags,
            double confidence) {
        String fragmentId = sourceDocument + ":" + sourceSection + ":" + fragmentType.name();
        return new KnowledgeFragment(
                fragmentId,
                fragmentType,
                title,
                content == null ? "" : content.trim(),
                sourceDocument,
                sourceSection,
                ContextSection.estimateTokensFromContent(content),
                tags,
                confidence);
    }
}
