package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.CharacterDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.CharacterSummaryDto;
import com.chugalkhorbandar.adapters.api.dto.CurrentLocationDto;
import com.chugalkhorbandar.adapters.api.dto.EntityReferenceDto;
import com.chugalkhorbandar.adapters.api.dto.OrganizationDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.OrganizationMembershipDto;
import com.chugalkhorbandar.adapters.api.dto.OrganizationSummaryDto;
import com.chugalkhorbandar.adapters.api.dto.PlaceDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.PlaceSummaryDto;
import com.chugalkhorbandar.adapters.api.dto.RelationshipDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.RelationshipSummaryDto;
import com.chugalkhorbandar.adapters.api.dto.StoryDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.StorySummaryDto;
import com.chugalkhorbandar.adapters.api.dto.TerritoryDetailsDto;
import com.chugalkhorbandar.adapters.api.dto.TerritorySummaryDto;
import com.chugalkhorbandar.adapters.api.dto.WorldStatusDto;
import com.chugalkhorbandar.application.query.CharacterQueryService.CharacterDetailsView;
import com.chugalkhorbandar.application.query.EntityReferenceResolver;
import com.chugalkhorbandar.application.query.OrganizationQueryService.OrganizationDetailsView;
import com.chugalkhorbandar.application.query.PlaceQueryService.PlaceDetailsView;
import com.chugalkhorbandar.application.query.RelationshipQueryService.RelationshipDetailsView;
import com.chugalkhorbandar.application.query.StoryQueryService.StoryDetailsView;
import com.chugalkhorbandar.application.query.TerritoryQueryService.TerritoryDetailsView;
import com.chugalkhorbandar.application.query.TextSectionSupport;
import com.chugalkhorbandar.application.query.WorldStatus;
import com.chugalkhorbandar.application.session.CharacterPresenceStore;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class WorldQueryDtoMapper {

    private final EntityReferenceResolver referenceResolver;
    private final CharacterPresenceStore characterPresenceStore;

    public WorldQueryDtoMapper(
            EntityReferenceResolver referenceResolver, CharacterPresenceStore characterPresenceStore) {
        this.referenceResolver = referenceResolver;
        this.characterPresenceStore = characterPresenceStore;
    }

    public WorldStatusDto toDto(WorldStatus status) {
        return new WorldStatusDto(
                status.status(),
                status.bootstrapVersion(),
                status.bootstrapTimestamp(),
                status.runtimeStartedAt(),
                status.persistenceProvider(),
                status.characters(),
                status.stories(),
                status.territories(),
                status.places(),
                status.organizations(),
                status.relationships(),
                status.timelineEntries(),
                status.charactersBySpecies(),
                status.storiesByEra());
    }

    public CharacterSummaryDto toSummaryDto(RuntimeCharacter character) {
        List<String> titles = TextSectionSupport.parseListItems(character.sections().get("titles"));
        if (titles.isEmpty() && character.title() != null) {
            titles = List.of(character.title());
        }
        String placeName = character.currentPlaceId() == null
                ? null
                : referenceResolver
                        .resolvePlace(character.currentPlaceId())
                        .map(EntityReferenceResolver.ResolvedReference::name)
                        .orElse(null);
        return new CharacterSummaryDto(
                character.id(),
                character.title(),
                TextSectionSupport.extractSpecies(character.sections()),
                titles,
                character.currentPlaceId(),
                placeName,
                characterPresenceStore.lastSeen(character.id()).orElse(null));
    }

    public CharacterDetailsDto toDetailsDto(CharacterDetailsView view) {
        RuntimeCharacter character = view.character();
        List<String> titles = TextSectionSupport.parseListItems(character.sections().get("titles"));
        if (titles.isEmpty() && character.title() != null) {
            titles = List.of(character.title());
        }
        Optional<EntityReferenceResolver.ResolvedReference> territory = view.currentTerritory();
        Optional<EntityReferenceResolver.ResolvedReference> place = view.currentPlace()
                .map(runtimePlace -> new EntityReferenceResolver.ResolvedReference(
                        runtimePlace.id(), runtimePlace.title(), "place"));
        return new CharacterDetailsDto(
                character.id(),
                character.title(),
                character.sections().getOrDefault("summary", ""),
                titles,
                character.sections().getOrDefault("history", ""),
                character.sections().getOrDefault("assets", ""),
                view.relationships().stream()
                        .map(relationship -> toRelationshipDto(relationship, character))
                        .toList(),
                character.preferences(),
                TextSectionSupport.extractPublicFacts(character.sections()),
                new CurrentLocationDto(
                        place.map(EntityReferenceResolver.ResolvedReference::id).orElse(character.currentPlaceId()),
                        place.map(EntityReferenceResolver.ResolvedReference::name).orElse(null),
                        territory.map(EntityReferenceResolver.ResolvedReference::id).orElse(null),
                        territory.map(EntityReferenceResolver.ResolvedReference::name).orElse(null)),
                territory.map(this::toEntityDto).orElse(null),
                view.organizations().stream()
                        .map(organization -> toOrganizationDto(organization, character.id()))
                        .toList());
    }

    public StorySummaryDto toSummaryDto(RuntimeStory story) {
        return new StorySummaryDto(
                story.id(),
                story.title(),
                story.sections().getOrDefault("summary", ""),
                story.sections().getOrDefault("era", ""));
    }

    public StoryDetailsDto toDetailsDto(StoryDetailsView view) {
        RuntimeStory story = view.story();
        return new StoryDetailsDto(
                story.id(),
                story.title(),
                story.sections().getOrDefault("summary", ""),
                story.sections().getOrDefault("era", ""),
                view.participants().stream().map(this::toEntityDto).toList(),
                view.places().stream().map(this::toEntityDto).toList(),
                TextSectionSupport.publicSections(story.sections()),
                story.linkedStories());
    }

    public TerritorySummaryDto toSummaryDto(RuntimeTerritory territory) {
        EntityReferenceDto ruler = referenceResolver
                .resolveCharacter(territory.currentRulerId())
                .map(this::toEntityDto)
                .orElse(new EntityReferenceDto(territory.currentRulerId(), territory.currentRulerId(), "character"));
        return new TerritorySummaryDto(territory.id(), territory.title(), ruler);
    }

    public TerritoryDetailsDto toDetailsDto(TerritoryDetailsView view) {
        RuntimeTerritory territory = view.territory();
        return new TerritoryDetailsDto(
                territory.id(),
                territory.title(),
                view.ruler().map(this::toEntityDto).orElse(null),
                view.ministers().stream().map(this::toEntityDto).toList(),
                view.places().stream().map(this::toEntityDto).toList(),
                TextSectionSupport.publicSections(territory.sections()));
    }

    public PlaceSummaryDto toSummaryDto(RuntimePlace place) {
        EntityReferenceDto territory = referenceResolver
                .resolveTerritoryForPlace(place)
                .map(this::toEntityDto)
                .orElse(null);
        return new PlaceSummaryDto(
                place.id(), place.title(), place.sections().getOrDefault("type", ""), territory);
    }

    public PlaceDetailsDto toDetailsDto(PlaceDetailsView view) {
        RuntimePlace place = view.place();
        return new PlaceDetailsDto(
                place.id(),
                place.title(),
                place.sections().getOrDefault("type", ""),
                view.territory().map(this::toEntityDto).orElse(null),
                view.owner().map(this::toEntityDto).orElse(null),
                view.connectedPlaces().stream().map(this::toEntityDto).toList(),
                TextSectionSupport.publicSections(place.sections()));
    }

    public OrganizationSummaryDto toSummaryDto(RuntimeOrganization organization) {
        return new OrganizationSummaryDto(
                organization.id(),
                organization.title(),
                organization.sections().getOrDefault("type", ""),
                referenceResolver
                        .resolveCharacter(organization.sections().get("leader"))
                        .map(this::toEntityDto)
                        .orElse(null));
    }

    public OrganizationDetailsDto toDetailsDto(OrganizationDetailsView view) {
        RuntimeOrganization organization = view.organization();
        return new OrganizationDetailsDto(
                organization.id(),
                organization.title(),
                organization.sections().getOrDefault("type", ""),
                view.leader().map(this::toEntityDto).orElse(null),
                view.headquarters().map(this::toEntityDto).orElse(null),
                view.members().stream().map(this::toEntityDto).toList(),
                TextSectionSupport.publicSections(organization.sections()));
    }

    public RelationshipDetailsDto toDetailsDto(RelationshipDetailsView view) {
        RuntimeRelationship relationship = view.relationship();
        return new RelationshipDetailsDto(
                relationship.id(),
                relationship.title(),
                relationship.sections().get("relationshipType"),
                relationship.sections().get("relationshipStatus"),
                relationship.sections().getOrDefault("description", ""),
                view.characters().stream().map(this::toEntityDto).toList());
    }

    private RelationshipSummaryDto toRelationshipDto(RuntimeRelationship relationship, RuntimeCharacter viewer) {
        EntityReferenceDto target = referenceResolver
                .otherCharacterInRelationship(relationship.sections(), viewer)
                .map(this::toEntityDto)
                .orElse(null);
        return new RelationshipSummaryDto(
                relationship.id(),
                relationship.title(),
                relationship.sections().get("relationshipType"),
                relationship.sections().get("relationshipStatus"),
                target);
    }

    private OrganizationMembershipDto toOrganizationDto(RuntimeOrganization organization, String characterId) {
        return new OrganizationMembershipDto(
                organization.id(), organization.title(), organization.roles().get(characterId));
    }

    private EntityReferenceDto toEntityDto(EntityReferenceResolver.ResolvedReference reference) {
        return new EntityReferenceDto(reference.id(), reference.name(), reference.type());
    }
}
