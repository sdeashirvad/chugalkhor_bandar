package com.chugalkhorbandar.application.context.knowledge;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.knowledge.provider.KnowledgeProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeFragmentPlanner {

    private final KnowledgeFragmentSelector selector;
    private final List<KnowledgeProvider> providers;

    public KnowledgeFragmentPlanner(KnowledgeFragmentSelector selector, List<KnowledgeProvider> providers) {
        this.selector = selector;
        this.providers = List.copyOf(providers);
    }

    public KnowledgeFragmentPlan plan(ContextPlannerRequest request) {
        Map<KnowledgeFragmentType, String> selections = selector.select(request.latestUserMessage());
        Set<KnowledgeFragmentType> selectedTypes = selections.keySet();
        List<KnowledgeFragmentRequest> requests = new ArrayList<>();
        for (KnowledgeProvider provider : providers) {
            requests.addAll(provider.plan(request, selectedTypes));
        }
        List<KnowledgeFragmentRequest> deduped = new ArrayList<>(dedupeRequests(requests));
        deduped.sort(Comparator.comparingInt(KnowledgeFragmentRequest::priority)
                .thenComparing(requestItem -> requestItem.fragmentType().name()));
        int totalTokens = deduped.stream()
                .mapToInt(item -> com.chugalkhorbandar.application.context.ContextSection.estimateTokens(item.reference()))
                .sum();
        return new KnowledgeFragmentPlan(
                deduped,
                totalTokens,
                selector.trace(selections));
    }

    private static List<KnowledgeFragmentRequest> dedupeRequests(List<KnowledgeFragmentRequest> requests) {
        Map<String, KnowledgeFragmentRequest> unique = new LinkedHashMap<>();
        for (KnowledgeFragmentRequest request : requests) {
            unique.putIfAbsent(
                    request.fragmentType().name() + ":" + request.reference().format(),
                    request);
        }
        return List.copyOf(unique.values());
    }
}
