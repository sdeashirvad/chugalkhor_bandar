package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CanonJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CharacterJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CustomJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.GlossaryEntryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.LawJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ObjectJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.OrganizationJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.PlaceJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.PromptProfileJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.RelationshipJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ResourceJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.StoryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.TerritoryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.TimelineEntryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.WorldRulesJpaRepository;
import com.chugalkhorbandar.domain.world.ports.CanonRepository;
import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.CustomRepository;
import com.chugalkhorbandar.domain.world.ports.GlossaryRepository;
import com.chugalkhorbandar.domain.world.ports.LawRepository;
import com.chugalkhorbandar.domain.world.ports.ObjectRepository;
import com.chugalkhorbandar.domain.world.ports.OrganizationRepository;
import com.chugalkhorbandar.domain.world.ports.PlaceRepository;
import com.chugalkhorbandar.domain.world.ports.PromptProfileRepository;
import com.chugalkhorbandar.domain.world.ports.RelationshipRepository;
import com.chugalkhorbandar.domain.world.ports.ResourceRepository;
import com.chugalkhorbandar.domain.world.ports.StoryRepository;
import com.chugalkhorbandar.domain.world.ports.TerritoryRepository;
import com.chugalkhorbandar.domain.world.ports.TimelineRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.WorldRulesRepository;

public final class PostgresWorldRepositoryProvider implements WorldRepositoryProvider {

    private final CharacterRepository characters;
    private final TerritoryRepository territories;
    private final PlaceRepository places;
    private final StoryRepository stories;
    private final RelationshipRepository relationships;
    private final OrganizationRepository organizations;
    private final ResourceRepository resources;
    private final ObjectRepository objects;
    private final TimelineRepository timeline;
    private final PromptProfileRepository promptProfiles;
    private final CanonRepository canon;
    private final WorldRulesRepository worldRules;
    private final LawRepository laws;
    private final CustomRepository customs;
    private final GlossaryRepository glossary;

    public PostgresWorldRepositoryProvider(
            CharacterJpaRepository characterJpa,
            TerritoryJpaRepository territoryJpa,
            PlaceJpaRepository placeJpa,
            StoryJpaRepository storyJpa,
            RelationshipJpaRepository relationshipJpa,
            OrganizationJpaRepository organizationJpa,
            ResourceJpaRepository resourceJpa,
            ObjectJpaRepository objectJpa,
            TimelineEntryJpaRepository timelineJpa,
            PromptProfileJpaRepository promptProfileJpa,
            CanonJpaRepository canonJpa,
            WorldRulesJpaRepository worldRulesJpa,
            LawJpaRepository lawJpa,
            CustomJpaRepository customJpa,
            GlossaryEntryJpaRepository glossaryJpa) {
        this.characters = new PostgresCharacterRepository(characterJpa);
        this.territories = new PostgresTerritoryRepository(territoryJpa);
        this.places = new PostgresPlaceRepository(placeJpa);
        this.stories = new PostgresStoryRepository(storyJpa);
        this.relationships = new PostgresRelationshipRepository(relationshipJpa);
        this.organizations = new PostgresOrganizationRepository(organizationJpa);
        this.resources = new PostgresResourceRepository(resourceJpa);
        this.objects = new PostgresObjectRepository(objectJpa);
        this.timeline = new PostgresTimelineRepository(timelineJpa);
        this.promptProfiles = new PostgresPromptProfileRepository(promptProfileJpa);
        this.canon = new PostgresCanonRepository(canonJpa);
        this.worldRules = new PostgresWorldRulesRepository(worldRulesJpa);
        this.laws = new PostgresLawRepository(lawJpa);
        this.customs = new PostgresCustomRepository(customJpa);
        this.glossary = new PostgresGlossaryRepository(glossaryJpa);
    }

    @Override
    public CharacterRepository characters() {
        return characters;
    }

    @Override
    public TerritoryRepository territories() {
        return territories;
    }

    @Override
    public PlaceRepository places() {
        return places;
    }

    @Override
    public StoryRepository stories() {
        return stories;
    }

    @Override
    public RelationshipRepository relationships() {
        return relationships;
    }

    @Override
    public OrganizationRepository organizations() {
        return organizations;
    }

    @Override
    public ResourceRepository resources() {
        return resources;
    }

    @Override
    public ObjectRepository objects() {
        return objects;
    }

    @Override
    public TimelineRepository timeline() {
        return timeline;
    }

    @Override
    public PromptProfileRepository promptProfiles() {
        return promptProfiles;
    }

    @Override
    public CanonRepository canon() {
        return canon;
    }

    @Override
    public WorldRulesRepository worldRules() {
        return worldRules;
    }

    @Override
    public LawRepository laws() {
        return laws;
    }

    @Override
    public CustomRepository customs() {
        return customs;
    }

    @Override
    public GlossaryRepository glossary() {
        return glossary;
    }
}
