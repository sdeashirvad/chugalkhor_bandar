package com.chugalkhorbandar.application.context.resolver;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import java.util.List;

public record ResolvedContext(
        List<ResolvedContextSection> sections,
        List<KnowledgeFragment> fragments,
        int totalEstimatedTokens) {

    public ResolvedContext {
        fragments = fragments == null ? List.of() : List.copyOf(fragments);
    }
}
