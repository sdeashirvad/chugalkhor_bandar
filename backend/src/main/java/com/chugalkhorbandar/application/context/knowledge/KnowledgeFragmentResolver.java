package com.chugalkhorbandar.application.context.knowledge;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.knowledge.provider.KnowledgeProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeFragmentResolver {

    private final List<KnowledgeProvider> providers;
    private final KnowledgeFragmentRegistry registry;

    public KnowledgeFragmentResolver(List<KnowledgeProvider> providers, KnowledgeFragmentRegistry registry) {
        this.providers = List.copyOf(providers);
        this.registry = registry;
    }

    public List<KnowledgeFragment> resolve(List<KnowledgeFragmentRequest> requests, ContextPlannerRequest context) {
        registry.clear();
        List<KnowledgeFragment> resolved = new ArrayList<>();
        for (KnowledgeFragmentRequest request : requests) {
            resolveRequest(request, context).ifPresent(resolved::add);
        }
        resolved.sort(Comparator.<KnowledgeFragment>comparingInt(
                        fragment -> KnowledgeFragmentPriorities.priority(fragment.fragmentType()))
                .thenComparing(KnowledgeFragment::fragmentId));
        registry.registerAll(resolved);
        return List.copyOf(resolved);
    }

    private java.util.Optional<KnowledgeFragment> resolveRequest(
            KnowledgeFragmentRequest request, ContextPlannerRequest context) {
        for (KnowledgeProvider provider : providers) {
            if (!provider.supportedFragmentTypes().contains(request.fragmentType())) {
                continue;
            }
            java.util.Optional<KnowledgeFragment> fragment = provider.resolve(request, context);
            if (fragment.isPresent()) {
                return fragment;
            }
        }
        return java.util.Optional.empty();
    }
}
