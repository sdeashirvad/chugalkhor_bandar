package com.chugalkhorbandar.application.context.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionPriorities;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class WorldContextProvider implements ContextProvider {

    private final WorldRepositoryProvider repositories;

    public WorldContextProvider(WorldRepositoryProvider repositories) {
        this.repositories = repositories;
    }

    @Override
    public String providerName() {
        return "canon";
    }

    @Override
    public Set<ContextSectionType> supportedTypes() {
        return Set.of(ContextSectionType.WORLD_CANON, ContextSectionType.WORLD_STATE, ContextSectionType.PUBLIC_EVENTS);
    }

    @Override
    public List<ContextSection> plan(ContextPlannerRequest request, Set<ContextSectionType> selectedTypes) {
        List<ContextSection> sections = new ArrayList<>();
        if (selectedTypes.contains(ContextSectionType.WORLD_CANON)) {
            String canonId = repositories.canon().findAll().stream()
                    .findFirst()
                    .map(canon -> canon.id())
                    .orElse("none");
            sections.add(section(
                    ContextSectionType.WORLD_CANON,
                    providerName(),
                    new ContextReference(providerName(), "canon", canonId, "sections", priority(ContextSectionType.WORLD_CANON))));
        }
        if (selectedTypes.contains(ContextSectionType.WORLD_STATE)) {
            sections.add(section(
                    ContextSectionType.WORLD_STATE,
                    "worldRuntime",
                    new ContextReference("worldRuntime", "worldState", "runtime", "snapshot", priority(ContextSectionType.WORLD_STATE))));
        }
        if (selectedTypes.contains(ContextSectionType.PUBLIC_EVENTS)) {
            sections.add(section(
                    ContextSectionType.PUBLIC_EVENTS,
                    "timeline",
                    new ContextReference("timeline", "timeline", "public-events", "unavailable", priority(ContextSectionType.PUBLIC_EVENTS))));
        }
        return sections;
    }

    @Override
    public boolean supports(ContextReference reference) {
        return Set.of("canon", "worldRuntime", "timeline").contains(reference.provider());
    }

    @Override
    public ResolvedContextSection resolve(ContextSection section, ContextPlannerRequest request) {
        return switch (section.type()) {
            case WORLD_CANON -> resolveCanon(section);
            case WORLD_STATE -> resolveWorldState(section, request);
            case PUBLIC_EVENTS -> ResolvedContextSection.from(section, "Public events are not available yet.");
            default -> ResolvedContextSection.from(section, "[missing: " + section.reference().entityId() + "]");
        };
    }

    private ResolvedContextSection resolveCanon(ContextSection section) {
        if ("none".equals(section.reference().entityId())) {
            return ResolvedContextSection.from(section, "Canon is not available.");
        }
        return repositories.canon().findById(section.reference().entityId())
                .map(canon -> ResolvedContextSection.from(
                        section,
                        canon.title()
                                + "\n"
                                + canon.sections().entrySet().stream()
                                        .map(entry -> entry.getKey() + ": " + entry.getValue().trim())
                                        .collect(Collectors.joining("\n"))))
                .orElseGet(() -> ResolvedContextSection.from(section, "[missing: " + section.reference().entityId() + "]"));
    }

    private static ResolvedContextSection resolveWorldState(ContextSection section, ContextPlannerRequest request) {
        var world = request.runtimeWorld();
        String content = "status=" + world.status()
                + ", bootstrapVersion=" + world.bootstrapVersion()
                + ", characters=" + world.characterCount()
                + ", stories=" + world.storyCount();
        return ResolvedContextSection.from(section, content);
    }

    private static int priority(ContextSectionType type) {
        return ContextSectionPriorities.priority(type);
    }
}
