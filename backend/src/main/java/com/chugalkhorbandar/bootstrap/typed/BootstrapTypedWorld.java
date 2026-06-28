package com.chugalkhorbandar.bootstrap.typed;

import com.chugalkhorbandar.bootstrap.typed.spec.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BootstrapTypedWorld {

    private final List<CharacterBootstrapSpec> characters;
    private final List<StoryBootstrapSpec> stories;
    private final List<PlaceBootstrapSpec> places;
    private final List<TerritoryBootstrapSpec> territories;
    private final List<OrganizationBootstrapSpec> organizations;
    private final List<ResourceBootstrapSpec> resources;
    private final List<ObjectBootstrapSpec> objects;
    private final List<RelationshipBootstrapSpec> relationships;
    private final List<LawBootstrapSpec> laws;
    private final List<CustomBootstrapSpec> customs;
    private final List<GlossaryEntryBootstrapSpec> glossaryEntries;
    private final List<PromptProfileBootstrapSpec> promptProfiles;
    private final List<CanonBootstrapSpec> canon;
    private final List<WorldRulesBootstrapSpec> worldRules;
    private final List<ChronologyBootstrapSpec> chronologyEntries;

    private BootstrapTypedWorld(Builder builder) {
        this.characters = List.copyOf(builder.characters);
        this.stories = List.copyOf(builder.stories);
        this.places = List.copyOf(builder.places);
        this.territories = List.copyOf(builder.territories);
        this.organizations = List.copyOf(builder.organizations);
        this.resources = List.copyOf(builder.resources);
        this.objects = List.copyOf(builder.objects);
        this.relationships = List.copyOf(builder.relationships);
        this.laws = List.copyOf(builder.laws);
        this.customs = List.copyOf(builder.customs);
        this.glossaryEntries = List.copyOf(builder.glossaryEntries);
        this.promptProfiles = List.copyOf(builder.promptProfiles);
        this.canon = List.copyOf(builder.canon);
        this.worldRules = List.copyOf(builder.worldRules);
        this.chronologyEntries = List.copyOf(builder.chronologyEntries);
    }

    public List<CharacterBootstrapSpec> characters() {
        return characters;
    }

    public List<StoryBootstrapSpec> stories() {
        return stories;
    }

    public List<PlaceBootstrapSpec> places() {
        return places;
    }

    public List<TerritoryBootstrapSpec> territories() {
        return territories;
    }

    public List<OrganizationBootstrapSpec> organizations() {
        return organizations;
    }

    public List<ResourceBootstrapSpec> resources() {
        return resources;
    }

    public List<ObjectBootstrapSpec> objects() {
        return objects;
    }

    public List<RelationshipBootstrapSpec> relationships() {
        return relationships;
    }

    public List<LawBootstrapSpec> laws() {
        return laws;
    }

    public List<CustomBootstrapSpec> customs() {
        return customs;
    }

    public List<GlossaryEntryBootstrapSpec> glossaryEntries() {
        return glossaryEntries;
    }

    public List<PromptProfileBootstrapSpec> promptProfiles() {
        return promptProfiles;
    }

    public List<CanonBootstrapSpec> canon() {
        return canon;
    }

    public List<WorldRulesBootstrapSpec> worldRules() {
        return worldRules;
    }

    public List<ChronologyBootstrapSpec> chronologyEntries() {
        return chronologyEntries;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<CharacterBootstrapSpec> characters = new ArrayList<>();
        private final List<StoryBootstrapSpec> stories = new ArrayList<>();
        private final List<PlaceBootstrapSpec> places = new ArrayList<>();
        private final List<TerritoryBootstrapSpec> territories = new ArrayList<>();
        private final List<OrganizationBootstrapSpec> organizations = new ArrayList<>();
        private final List<ResourceBootstrapSpec> resources = new ArrayList<>();
        private final List<ObjectBootstrapSpec> objects = new ArrayList<>();
        private final List<RelationshipBootstrapSpec> relationships = new ArrayList<>();
        private final List<LawBootstrapSpec> laws = new ArrayList<>();
        private final List<CustomBootstrapSpec> customs = new ArrayList<>();
        private final List<GlossaryEntryBootstrapSpec> glossaryEntries = new ArrayList<>();
        private final List<PromptProfileBootstrapSpec> promptProfiles = new ArrayList<>();
        private final List<CanonBootstrapSpec> canon = new ArrayList<>();
        private final List<WorldRulesBootstrapSpec> worldRules = new ArrayList<>();
        private final List<ChronologyBootstrapSpec> chronologyEntries = new ArrayList<>();

        public Builder addCharacter(CharacterBootstrapSpec spec) {
            characters.add(spec);
            return this;
        }

        public Builder addStory(StoryBootstrapSpec spec) {
            stories.add(spec);
            return this;
        }

        public Builder addPlace(PlaceBootstrapSpec spec) {
            places.add(spec);
            return this;
        }

        public Builder addTerritory(TerritoryBootstrapSpec spec) {
            territories.add(spec);
            return this;
        }

        public Builder addOrganization(OrganizationBootstrapSpec spec) {
            organizations.add(spec);
            return this;
        }

        public Builder addResource(ResourceBootstrapSpec spec) {
            resources.add(spec);
            return this;
        }

        public Builder addObject(ObjectBootstrapSpec spec) {
            objects.add(spec);
            return this;
        }

        public Builder addRelationship(RelationshipBootstrapSpec spec) {
            relationships.add(spec);
            return this;
        }

        public Builder addLaw(LawBootstrapSpec spec) {
            laws.add(spec);
            return this;
        }

        public Builder addCustom(CustomBootstrapSpec spec) {
            customs.add(spec);
            return this;
        }

        public Builder addGlossaryEntry(GlossaryEntryBootstrapSpec spec) {
            glossaryEntries.add(spec);
            return this;
        }

        public Builder addPromptProfile(PromptProfileBootstrapSpec spec) {
            promptProfiles.add(spec);
            return this;
        }

        public Builder addCanon(CanonBootstrapSpec spec) {
            canon.add(spec);
            return this;
        }

        public Builder addWorldRules(WorldRulesBootstrapSpec spec) {
            worldRules.add(spec);
            return this;
        }

        public Builder addChronology(ChronologyBootstrapSpec spec) {
            chronologyEntries.add(spec);
            return this;
        }

        public BootstrapTypedWorld build() {
            return new BootstrapTypedWorld(this);
        }
    }
}
