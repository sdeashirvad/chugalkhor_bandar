package com.chugalkhorbandar.application.context.knowledge.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentMappings;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPriorities;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class StoryKnowledgeProvider implements KnowledgeProvider {

    private static final Set<KnowledgeFragmentType> SUPPORTED = Set.of(KnowledgeFragmentType.STORY_SUMMARY);

    private final WorldRepositoryProvider repositories;

    public StoryKnowledgeProvider(WorldRepositoryProvider repositories) {
        this.repositories = repositories;
    }

    @Override
    public String providerName() {
        return "storyKnowledge";
    }

    @Override
    public Set<KnowledgeFragmentType> supportedFragmentTypes() {
        return SUPPORTED;
    }

    @Override
    public List<KnowledgeFragmentRequest> plan(ContextPlannerRequest request, Set<KnowledgeFragmentType> selectedTypes) {
        if (!selectedTypes.contains(KnowledgeFragmentType.STORY_SUMMARY)) {
            return List.of();
        }
        List<KnowledgeFragmentRequest> requests = new ArrayList<>();
        repositories.stories().findAll(StoryQuery.all()).stream().limit(3).forEach(story -> requests.add(new KnowledgeFragmentRequest(
                KnowledgeFragmentType.STORY_SUMMARY,
                "Relevant story fragment",
                KnowledgeFragmentPriorities.priority(KnowledgeFragmentType.STORY_SUMMARY),
                new ContextReference(
                        providerName(),
                        "story",
                        story.id(),
                        "summary",
                        KnowledgeFragmentPriorities.priority(KnowledgeFragmentType.STORY_SUMMARY)))));
        return requests;
    }

    @Override
    public Optional<KnowledgeFragment> resolve(KnowledgeFragmentRequest request, ContextPlannerRequest context) {
        return repositories.stories().findById(request.reference().entityId()).map(story -> {
            String summary = story.sections().getOrDefault("summary", story.title());
            return KnowledgeFragment.of(
                    KnowledgeFragmentType.STORY_SUMMARY,
                    story.title(),
                    summary,
                    story.id(),
                    "summary",
                    KnowledgeFragmentMappings.tagsFor(KnowledgeFragmentType.STORY_SUMMARY, story.id()),
                    1.0);
        });
    }
}
