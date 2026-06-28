package com.chugalkhorbandar.application.context.knowledge.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentMappings;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPriorities;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class WorldKnowledgeProvider implements KnowledgeProvider {

    private static final Set<KnowledgeFragmentType> SUPPORTED = Set.of(
            KnowledgeFragmentType.WORLD_GEOGRAPHY,
            KnowledgeFragmentType.WORLD_HISTORY,
            KnowledgeFragmentType.WORLD_POLITICS,
            KnowledgeFragmentType.WORLD_SPECIES,
            KnowledgeFragmentType.WORLD_ECONOMY,
            KnowledgeFragmentType.WORLD_TRANSPORT,
            KnowledgeFragmentType.TIMELINE);

    private final WorldRepositoryProvider repositories;

    public WorldKnowledgeProvider(WorldRepositoryProvider repositories) {
        this.repositories = repositories;
    }

    @Override
    public String providerName() {
        return "worldKnowledge";
    }

    @Override
    public Set<KnowledgeFragmentType> supportedFragmentTypes() {
        return SUPPORTED;
    }

    @Override
    public List<KnowledgeFragmentRequest> plan(ContextPlannerRequest request, Set<KnowledgeFragmentType> selectedTypes) {
        String canonId = repositories.canon().findAll().stream()
                .findFirst()
                .map(canon -> canon.id())
                .orElse("none");
        List<KnowledgeFragmentRequest> requests = new ArrayList<>();
        for (KnowledgeFragmentType type : selectedTypes) {
            if (!SUPPORTED.contains(type)) {
                continue;
            }
            requests.add(new KnowledgeFragmentRequest(
                    type,
                    "World knowledge fragment",
                    KnowledgeFragmentPriorities.priority(type),
                    new ContextReference(
                            providerName(),
                            "canon",
                            canonId,
                            type.name().toLowerCase(),
                            KnowledgeFragmentPriorities.priority(type))));
        }
        return requests;
    }

    @Override
    public Optional<KnowledgeFragment> resolve(KnowledgeFragmentRequest request, ContextPlannerRequest context) {
        if ("none".equals(request.reference().entityId())) {
            return Optional.empty();
        }
        return repositories.canon().findById(request.reference().entityId()).flatMap(canon -> canon.sections().entrySet().stream()
                .map(entry -> Map.entry(KnowledgeFragmentMappings.canonSectionType(entry.getKey()), entry))
                .filter(entry -> entry.getKey() == request.fragmentType())
                .findFirst()
                .map(entry -> KnowledgeFragment.of(
                        request.fragmentType(),
                        titleFor(request.fragmentType()),
                        entry.getValue().getValue(),
                        canon.id(),
                        entry.getValue().getKey(),
                        KnowledgeFragmentMappings.tagsFor(request.fragmentType(), canon.id()),
                        1.0)));
    }

    private static String titleFor(KnowledgeFragmentType type) {
        return switch (type) {
            case WORLD_GEOGRAPHY -> "World Geography";
            case WORLD_HISTORY -> "World History";
            case WORLD_POLITICS -> "World Politics";
            case WORLD_SPECIES -> "World Species";
            case WORLD_ECONOMY -> "World Economy";
            case WORLD_TRANSPORT -> "World Transport";
            case TIMELINE -> "Timeline";
            default -> type.name();
        };
    }
}
