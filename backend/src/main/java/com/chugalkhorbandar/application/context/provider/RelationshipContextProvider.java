package com.chugalkhorbandar.application.context.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionPriorities;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.RelationshipQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RelationshipContextProvider implements ContextProvider {

    private final WorldRepositoryProvider repositories;

    public RelationshipContextProvider(WorldRepositoryProvider repositories) {
        this.repositories = repositories;
    }

    @Override
    public String providerName() {
        return "relationships";
    }

    @Override
    public Set<ContextSectionType> supportedTypes() {
        return Set.of(ContextSectionType.RELATIONSHIPS);
    }

    @Override
    public List<ContextSection> plan(ContextPlannerRequest request, Set<ContextSectionType> selectedTypes) {
        if (!selectedTypes.contains(ContextSectionType.RELATIONSHIPS)) {
            return List.of();
        }
        return List.of(section(
                ContextSectionType.RELATIONSHIPS,
                providerName(),
                new ContextReference(
                        providerName(),
                        "relationships",
                        request.currentCharacter().id(),
                        "character",
                        priority(ContextSectionType.RELATIONSHIPS))));
    }

    @Override
    public ResolvedContextSection resolve(ContextSection section, ContextPlannerRequest request) {
        String characterId = section.reference().entityId();
        List<RuntimeRelationship> matches = repositories.relationships().findAll(RelationshipQuery.all()).stream()
                .filter(relationship -> relationship.sections().values().stream().anyMatch(value -> value.contains(characterId)))
                .limit(5)
                .toList();
        if (matches.isEmpty()) {
            return ResolvedContextSection.from(section, "No relationships found for character " + characterId + ".");
        }
        String content = matches.stream()
                .map(RuntimeRelationship::title)
                .collect(Collectors.joining("\n"));
        return ResolvedContextSection.from(section, content);
    }

    private static int priority(ContextSectionType type) {
        return ContextSectionPriorities.priority(type);
    }
}
