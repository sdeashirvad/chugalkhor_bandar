package com.chugalkhorbandar.application.context.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionPriorities;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class StoryContextProvider implements ContextProvider {

    private final WorldRepositoryProvider repositories;

    public StoryContextProvider(WorldRepositoryProvider repositories) {
        this.repositories = repositories;
    }

    @Override
    public String providerName() {
        return "stories";
    }

    @Override
    public Set<ContextSectionType> supportedTypes() {
        return Set.of(ContextSectionType.RELEVANT_STORIES);
    }

    @Override
    public List<ContextSection> plan(ContextPlannerRequest request, Set<ContextSectionType> selectedTypes) {
        if (!selectedTypes.contains(ContextSectionType.RELEVANT_STORIES)) {
            return List.of();
        }
        List<RuntimeStory> stories = repositories.stories().findAll(StoryQuery.all()).stream()
                .sorted(Comparator.comparing(RuntimeStory::title, String.CASE_INSENSITIVE_ORDER))
                .limit(3)
                .toList();
        List<ContextSection> sections = new ArrayList<>();
        if (stories.isEmpty()) {
            sections.add(section(
                    ContextSectionType.RELEVANT_STORIES,
                    providerName(),
                    new ContextReference(providerName(), "story", "none", "summary", priority(ContextSectionType.RELEVANT_STORIES))));
        } else {
            for (RuntimeStory story : stories) {
                sections.add(section(
                        ContextSectionType.RELEVANT_STORIES,
                        providerName(),
                        new ContextReference(providerName(), "story", story.id(), "summary", priority(ContextSectionType.RELEVANT_STORIES))));
            }
        }
        return sections;
    }

    @Override
    public ResolvedContextSection resolve(ContextSection section, ContextPlannerRequest request) {
        if ("none".equals(section.reference().entityId())) {
            return ResolvedContextSection.from(section, "No stories available.");
        }
        return repositories.stories().findById(section.reference().entityId())
                .map(story -> {
                    String summary = story.sections().getOrDefault("summary", "").trim();
                    String content = story.title() + (summary.isBlank() ? "" : "\n" + summary);
                    return ResolvedContextSection.from(section, content);
                })
                .orElseGet(() -> ResolvedContextSection.from(section, "[missing: " + section.reference().entityId() + "]"));
    }

    private static int priority(ContextSectionType type) {
        return ContextSectionPriorities.priority(type);
    }
}
