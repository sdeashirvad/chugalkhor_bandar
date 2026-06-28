package com.chugalkhorbandar.adapters.persistence.memory;

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

public final class InMemoryWorldRepositoryProvider implements WorldRepositoryProvider {

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

    public InMemoryWorldRepositoryProvider(InMemoryWorldStore store) {
        this.characters = new InMemoryCharacterRepository(store);
        this.territories = new InMemoryTerritoryRepository(store);
        this.places = new InMemoryPlaceRepository(store);
        this.stories = new InMemoryStoryRepository(store);
        this.relationships = new InMemoryRelationshipRepository(store);
        this.organizations = new InMemoryOrganizationRepository(store);
        this.resources = new InMemoryResourceRepository(store);
        this.objects = new InMemoryObjectRepository(store);
        this.timeline = new InMemoryTimelineRepository(store);
        this.promptProfiles = new InMemoryPromptProfileRepository(store);
        this.canon = new InMemoryCanonRepository(store);
        this.worldRules = new InMemoryWorldRulesRepository(store);
        this.laws = new InMemoryLawRepository(store);
        this.customs = new InMemoryCustomRepository(store);
        this.glossary = new InMemoryGlossaryRepository(store);
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
