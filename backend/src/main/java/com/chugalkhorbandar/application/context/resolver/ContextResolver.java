package com.chugalkhorbandar.application.context.resolver;

import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentLegacyAdapter;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentResolver;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ContextResolver {

    private final KnowledgeFragmentResolver knowledgeFragmentResolver;

    public ContextResolver(KnowledgeFragmentResolver knowledgeFragmentResolver) {
        this.knowledgeFragmentResolver = knowledgeFragmentResolver;
    }

    public ResolvedContext resolve(ContextPlan plan, ContextPlannerRequest request) {
        List<KnowledgeFragment> fragments =
                knowledgeFragmentResolver.resolve(plan.fragmentPlan().requests(), request);
        List<ResolvedContextSection> sections = KnowledgeFragmentLegacyAdapter.toResolvedSections(fragments, plan.fragmentPlan());
        int totalTokens = fragments.stream().mapToInt(KnowledgeFragment::estimatedTokens).sum();
        return new ResolvedContext(List.copyOf(sections), fragments, totalTokens);
    }
}
