package com.chugalkhorbandar.application.query;

import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.OrganizationRepository;
import com.chugalkhorbandar.domain.world.ports.PlaceRepository;
import com.chugalkhorbandar.domain.world.ports.RelationshipRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.ports.query.RelationshipQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CharacterQueryService {

    private final CharacterRepository characters;
    private final RelationshipRepository relationships;
    private final OrganizationRepository organizations;
    private final PlaceRepository places;
    private final EntityReferenceResolver referenceResolver;

    public CharacterQueryService(WorldRepositoryProvider repositoryProvider, EntityReferenceResolver referenceResolver) {
        this.characters = repositoryProvider.characters();
        this.relationships = repositoryProvider.relationships();
        this.organizations = repositoryProvider.organizations();
        this.places = repositoryProvider.places();
        this.referenceResolver = referenceResolver;
    }

    public List<RuntimeCharacter> findAll() {
        return characters.findAll(CharacterQuery.all()).stream()
                .sorted(Comparator.comparing(RuntimeCharacter::title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public RuntimeCharacter findById(String id) {
        return characters.findById(id).orElseThrow(() -> new ResourceNotFoundException("Character", id));
    }

    public List<RuntimeCharacter> findByTitle(String title) {
        return characters.findAll(CharacterQuery.withTitleContaining(title)).stream()
                .sorted(Comparator.comparing(RuntimeCharacter::title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<RuntimeCharacter> findByPlace(String placeId) {
        return characters.findAll(CharacterQuery.atPlace(placeId)).stream()
                .sorted(Comparator.comparing(RuntimeCharacter::title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public CharacterDetailsView findDetailsById(String id) {
        RuntimeCharacter character = findById(id);
        List<RuntimeRelationship> characterRelationships = relationships.findAll(RelationshipQuery.all()).stream()
                .filter(relationship -> involvesCharacter(relationship, character))
                .toList();
        List<RuntimeOrganization> characterOrganizations = organizations.findAll().stream()
                .filter(organization -> organization.roles().containsKey(id))
                .toList();
        Optional<RuntimePlace> currentPlace = character.currentPlaceId() == null
                ? Optional.empty()
                : places.findById(character.currentPlaceId());
        Optional<EntityReferenceResolver.ResolvedReference> currentTerritory = currentPlace.flatMap(
                place -> referenceResolver.resolveTerritoryForPlace(place));
        return new CharacterDetailsView(
                character, characterRelationships, characterOrganizations, currentPlace, currentTerritory);
    }

    private static boolean involvesCharacter(RuntimeRelationship relationship, RuntimeCharacter character) {
        if (relationship.sections().values().stream().anyMatch(value -> value.contains(character.id()))) {
            return true;
        }
        String charactersSection = relationship.sections().get("characters");
        if (charactersSection == null) {
            return false;
        }
        return TextSectionSupport.parseListItems(charactersSection).stream()
                .anyMatch(name -> name.equalsIgnoreCase(character.title()));
    }

    public record CharacterDetailsView(
            RuntimeCharacter character,
            List<RuntimeRelationship> relationships,
            List<RuntimeOrganization> organizations,
            Optional<RuntimePlace> currentPlace,
            Optional<EntityReferenceResolver.ResolvedReference> currentTerritory) {}
}
