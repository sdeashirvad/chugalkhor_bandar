package com.chugalkhorbandar.application.context.resolver;

import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionType;

public record ResolvedContextSection(
        ContextSectionType type,
        int priority,
        String source,
        ContextReference reference,
        String content,
        int estimatedTokens) {

    public static ResolvedContextSection from(ContextSection section, String content) {
        return new ResolvedContextSection(
                section.type(),
                section.priority(),
                section.source(),
                section.reference(),
                content,
                ContextSection.estimateTokensFromContent(content));
    }

    public String contentReference() {
        return reference.format();
    }
}
