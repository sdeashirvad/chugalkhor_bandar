package com.chugalkhorbandar.application.query;

import com.chugalkhorbandar.domain.world.ports.PlaceRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PlaceQueryService {

    private final PlaceRepository places;
    private final EntityReferenceResolver referenceResolver;

    public PlaceQueryService(WorldRepositoryProvider repositoryProvider, EntityReferenceResolver referenceResolver) {
        this.places = repositoryProvider.places();
        this.referenceResolver = referenceResolver;
    }

    public List<RuntimePlace> findAll() {
        return places.findAll().stream()
                .sorted(Comparator.comparing(RuntimePlace::title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public RuntimePlace findById(String id) {
        return places.findById(id).orElseThrow(() -> new ResourceNotFoundException("Place", id));
    }

    public PlaceDetailsView findDetailsById(String id) {
        RuntimePlace place = findById(id);
        return new PlaceDetailsView(
                place,
                referenceResolver.resolveTerritoryForPlace(place),
                referenceResolver.resolveCharacter(place.sections().get("currentOwner")),
                referenceResolver.resolveListItems(place.sections().get("connectedPlaces"), EntityReferenceResolver.ReferenceType.PLACE));
    }

    public record PlaceDetailsView(
            RuntimePlace place,
            java.util.Optional<EntityReferenceResolver.ResolvedReference> territory,
            java.util.Optional<EntityReferenceResolver.ResolvedReference> owner,
            List<EntityReferenceResolver.ResolvedReference> connectedPlaces) {}
}
