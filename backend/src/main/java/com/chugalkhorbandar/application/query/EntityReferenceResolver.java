package com.chugalkhorbandar.application.query;

import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.OrganizationRepository;
import com.chugalkhorbandar.domain.world.ports.PlaceRepository;
import com.chugalkhorbandar.domain.world.ports.StoryRepository;
import com.chugalkhorbandar.domain.world.ports.TerritoryRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EntityReferenceResolver {

    private final WorldRepositoryProvider repositories;

    public EntityReferenceResolver(WorldRepositoryProvider repositories) {
        this.repositories = repositories;
    }

    private CharacterRepository characters() {
        return repositories.characters();
    }

    private PlaceRepository places() {
        return repositories.places();
    }

    private TerritoryRepository territories() {
        return repositories.territories();
    }

    private OrganizationRepository organizations() {
        return repositories.organizations();
    }

    private StoryRepository stories() {
        return repositories.stories();
    }

    public Optional<ResolvedReference> resolveCharacter(String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            return Optional.empty();
        }
        Optional<RuntimeCharacter> byId = characters().findById(idOrName.trim());
        if (byId.isPresent()) {
            return Optional.of(toReference(byId.get().id(), byId.get().title(), "character"));
        }
        return characters().findAll(CharacterQuery.all()).stream()
                .filter(character -> namesMatch(character.title(), idOrName))
                .findFirst()
                .map(character -> toReference(character.id(), character.title(), "character"));
    }

    public Optional<ResolvedReference> resolvePlace(String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            return Optional.empty();
        }
        Optional<RuntimePlace> byId = places().findById(idOrName.trim());
        if (byId.isPresent()) {
            return Optional.of(toReference(byId.get().id(), byId.get().title(), "place"));
        }
        return places().findAll().stream()
                .filter(place -> namesMatch(place.title(), idOrName))
                .findFirst()
                .map(place -> toReference(place.id(), place.title(), "place"));
    }

    public Optional<ResolvedReference> resolveTerritory(String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            return Optional.empty();
        }
        Optional<RuntimeTerritory> byId = territories().findById(idOrName.trim());
        if (byId.isPresent()) {
            return Optional.of(toReference(byId.get().id(), byId.get().title(), "territory"));
        }
        return territories().findAll().stream()
                .filter(territory -> namesMatch(territory.title(), idOrName))
                .findFirst()
                .map(territory -> toReference(territory.id(), territory.title(), "territory"));
    }

    public Optional<ResolvedReference> resolveOrganization(String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            return Optional.empty();
        }
        Optional<RuntimeOrganization> byId = organizations().findById(idOrName.trim());
        if (byId.isPresent()) {
            return Optional.of(toReference(byId.get().id(), byId.get().title(), "organization"));
        }
        return organizations().findAll().stream()
                .filter(organization -> namesMatch(organization.title(), idOrName))
                .findFirst()
                .map(organization -> toReference(organization.id(), organization.title(), "organization"));
    }

    public Optional<ResolvedReference> resolveStory(String idOrName) {
        if (idOrName == null || idOrName.isBlank()) {
            return Optional.empty();
        }
        Optional<RuntimeStory> byId = stories().findById(idOrName.trim());
        if (byId.isPresent()) {
            return Optional.of(toReference(byId.get().id(), byId.get().title(), "story"));
        }
        return stories().findAll(StoryQuery.all()).stream()
                .filter(story -> namesMatch(story.title(), idOrName))
                .findFirst()
                .map(story -> toReference(story.id(), story.title(), "story"));
    }

    public Optional<ResolvedReference> resolveTerritoryForPlace(RuntimePlace place) {
        if (place == null) {
            return Optional.empty();
        }
        String locatedIn = place.sections().get("locatedIn");
        if (locatedIn != null && !locatedIn.isBlank()) {
            Optional<ResolvedReference> territory = resolveTerritory(locatedIn.trim());
            if (territory.isPresent()) {
                return territory;
            }
            Optional<ResolvedReference> viaParentPlace = resolvePlace(locatedIn.trim())
                    .flatMap(ref -> places().findById(ref.id()))
                    .flatMap(this::territoryForCapitalPlace);
            if (viaParentPlace.isPresent()) {
                return viaParentPlace;
            }
        }
        Optional<ResolvedReference> directCapital = territoryForCapitalPlace(place);
        if (directCapital.isPresent()) {
            return directCapital;
        }
        return Optional.empty();
    }

    private Optional<ResolvedReference> territoryForCapitalPlace(RuntimePlace place) {
        return territories().findAll().stream()
                .filter(territory -> namesMatch(territory.sections().get("capital"), place.title()))
                .findFirst()
                .map(territory -> toReference(territory.id(), territory.title(), "territory"));
    }

    public List<ResolvedReference> resolveListItems(String sectionText, ReferenceType type) {
        List<ResolvedReference> resolved = new ArrayList<>();
        for (String item : TextSectionSupport.parseListItems(sectionText)) {
            resolveByType(item, type).ifPresent(resolved::add);
        }
        return List.copyOf(resolved);
    }

    public List<ResolvedReference> resolveMentionedNames(String sectionText, ReferenceType type) {
        List<ResolvedReference> resolved = new ArrayList<>();
        for (String item : TextSectionSupport.parseListItems(sectionText)) {
            resolveByType(item, type).ifPresent(resolved::add);
        }
        if (resolved.isEmpty() && sectionText != null && !sectionText.isBlank()) {
            resolveByType(sectionText.trim(), type).ifPresent(resolved::add);
        }
        return List.copyOf(resolved);
    }

    public Optional<ResolvedReference> otherCharacterInRelationship(
            Map<String, String> sections, RuntimeCharacter viewer) {
        List<String> names = TextSectionSupport.parseListItems(sections.get("characters"));
        for (String name : names) {
            if (!namesMatch(name, viewer.title()) && !name.equals(viewer.id())) {
                return resolveCharacter(name);
            }
        }
        for (String value : sections.values()) {
            if (value.contains(viewer.id())) {
                continue;
            }
            Optional<ResolvedReference> match = resolveCharacter(value);
            if (match.isPresent() && !match.get().id().equals(viewer.id())) {
                return match;
            }
        }
        return Optional.empty();
    }

    public List<RuntimePlace> placesInTerritory(RuntimeTerritory territory) {
        return places().findAll().stream()
                .filter(place -> placeBelongsToTerritory(place, territory))
                .toList();
    }

    private boolean placeBelongsToTerritory(RuntimePlace place, RuntimeTerritory territory) {
        String locatedIn = place.sections().get("locatedIn");
        if (locatedIn != null && namesMatch(locatedIn, territory.title())) {
            return true;
        }
        String capital = territory.sections().get("capital");
        return capital != null && namesMatch(capital, place.title());
    }

    private Optional<ResolvedReference> resolveByType(String value, ReferenceType type) {
        return switch (type) {
            case CHARACTER -> resolveCharacter(value);
            case PLACE -> resolvePlace(value);
            case TERRITORY -> resolveTerritory(value);
            case ORGANIZATION -> resolveOrganization(value);
            case STORY -> resolveStory(value);
        };
    }

    private static ResolvedReference toReference(String id, String name, String type) {
        return new ResolvedReference(id, name, type);
    }

    private static boolean namesMatch(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        return left.trim().equalsIgnoreCase(right.trim());
    }

    public enum ReferenceType {
        CHARACTER,
        PLACE,
        TERRITORY,
        ORGANIZATION,
        STORY
    }

    public record ResolvedReference(String id, String name, String type) {}
}
