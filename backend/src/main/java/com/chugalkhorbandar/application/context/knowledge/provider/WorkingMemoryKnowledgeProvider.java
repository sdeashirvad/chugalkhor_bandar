package com.chugalkhorbandar.application.context.knowledge.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPriorities;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryNarrator;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.memory.working.WorkingMemorySnapshot;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class WorkingMemoryKnowledgeProvider implements KnowledgeProvider {

    private static final Set<KnowledgeFragmentType> SUPPORTED = Set.of(KnowledgeFragmentType.WORKING_MEMORY);

    private final WorkingMemoryService workingMemoryService;

    public WorkingMemoryKnowledgeProvider(WorkingMemoryService workingMemoryService) {
        this.workingMemoryService = workingMemoryService;
    }

    @Override
    public String providerName() {
        return "workingMemoryKnowledge";
    }

    @Override
    public Set<KnowledgeFragmentType> supportedFragmentTypes() {
        return SUPPORTED;
    }

    @Override
    public List<KnowledgeFragmentRequest> plan(ContextPlannerRequest request, Set<KnowledgeFragmentType> selectedTypes) {
        if (!selectedTypes.contains(KnowledgeFragmentType.WORKING_MEMORY)) {
            return List.of();
        }
        String sessionId = request.session().sessionId();
        return List.of(new KnowledgeFragmentRequest(
                KnowledgeFragmentType.WORKING_MEMORY,
                "Session working memory",
                KnowledgeFragmentPriorities.priority(KnowledgeFragmentType.WORKING_MEMORY),
                new ContextReference(
                        providerName(),
                        "workingMemory",
                        sessionId,
                        "snapshot",
                        KnowledgeFragmentPriorities.priority(KnowledgeFragmentType.WORKING_MEMORY))));
    }

    @Override
    public Optional<KnowledgeFragment> resolve(KnowledgeFragmentRequest request, ContextPlannerRequest context) {
        WorkingMemorySnapshot snapshot = workingMemoryService
                .find(context.session().sessionId())
                .orElseGet(() -> workingMemoryService.rebuildFromContext(context));
        String content = WorkingMemoryNarrator.narrate(snapshot.memory());
        if (content.isBlank()) {
            content = "No working memory yet for this session.";
        }
        return Optional.of(KnowledgeFragment.of(
                KnowledgeFragmentType.WORKING_MEMORY,
                "Working Memory",
                content,
                context.session().sessionId(),
                "snapshot",
                Set.of("workingMemory"),
                1.0));
    }
}
