package com.chugalkhorbandar.application.query;

import com.chugalkhorbandar.domain.world.ports.TerritoryRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TerritoryQueryService {

    private final TerritoryRepository territories;
    private final EntityReferenceResolver referenceResolver;

    public TerritoryQueryService(WorldRepositoryProvider repositoryProvider, EntityReferenceResolver referenceResolver) {
        this.territories = repositoryProvider.territories();
        this.referenceResolver = referenceResolver;
    }

    public List<RuntimeTerritory> findAll() {
        return territories.findAll().stream()
                .sorted(Comparator.comparing(RuntimeTerritory::title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public RuntimeTerritory findById(String id) {
        return territories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Territory", id));
    }

    public TerritoryDetailsView findDetailsById(String id) {
        RuntimeTerritory territory = findById(id);
        List<EntityReferenceResolver.ResolvedReference> ministers = referenceResolver.resolveListItems(
                territory.sections().get("ministers"), EntityReferenceResolver.ReferenceType.CHARACTER);
        if (ministers.isEmpty()) {
            ministers = referenceResolver.resolveListItems(
                    territory.sections().get("knownMinisters"), EntityReferenceResolver.ReferenceType.CHARACTER);
        }
        List<EntityReferenceResolver.ResolvedReference> territoryPlaces = referenceResolver.placesInTerritory(territory).stream()
                .map(place -> new EntityReferenceResolver.ResolvedReference(place.id(), place.title(), "place"))
                .toList();
        return new TerritoryDetailsView(
                territory,
                referenceResolver.resolveCharacter(territory.currentRulerId()),
                ministers,
                territoryPlaces);
    }

    public record TerritoryDetailsView(
            RuntimeTerritory territory,
            java.util.Optional<EntityReferenceResolver.ResolvedReference> ruler,
            List<EntityReferenceResolver.ResolvedReference> ministers,
            List<EntityReferenceResolver.ResolvedReference> places) {}
}
