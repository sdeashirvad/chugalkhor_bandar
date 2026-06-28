package com.chugalkhorbandar.adapters.persistence;

import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCanon;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCustom;
import com.chugalkhorbandar.domain.world.runtime.RuntimeGlossaryEntry;
import com.chugalkhorbandar.domain.world.runtime.RuntimeLaw;
import com.chugalkhorbandar.domain.world.runtime.RuntimeObject;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import com.chugalkhorbandar.domain.world.runtime.RuntimeResource;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTimelineEntry;
import com.chugalkhorbandar.domain.world.runtime.RuntimeWorldRules;
import com.chugalkhorbandar.domain.world.runtime.WorldState;

public final class WorldStatePersister {

    private WorldStatePersister() {}

    public static void persist(WorldRepositoryProvider provider, WorldState state) {
        state.canon().values().forEach(canon -> persistCanon(provider, canon));
        state.worldRules().values().forEach(rules -> persistWorldRules(provider, rules));
        state.promptProfiles().values().forEach(profile -> persistPromptProfile(provider, profile));
        state.territories().values().forEach(territory -> persistTerritory(provider, territory));
        state.places().values().forEach(place -> persistPlace(provider, place));
        state.organizations().values().forEach(organization -> persistOrganization(provider, organization));
        state.resources().values().forEach(resource -> persistResource(provider, resource));
        state.objects().values().forEach(object -> persistObject(provider, object));
        state.characters().values().forEach(character -> persistCharacter(provider, character));
        state.relationships().values().forEach(relationship -> persistRelationship(provider, relationship));
        state.stories().values().forEach(story -> persistStory(provider, story));
        state.timeline().values().forEach(entry -> persistTimeline(provider, entry));
        state.laws().values().forEach(law -> persistLaw(provider, law));
        state.customs().values().forEach(custom -> persistCustom(provider, custom));
        state.glossary().values().forEach(entry -> persistGlossary(provider, entry));
    }

    private static void persistCharacter(WorldRepositoryProvider provider, RuntimeCharacter character) {
        if (provider.characters().exists(character.id())) {
            provider.characters().update(character);
        } else {
            provider.characters().create(character);
        }
    }

    private static void persistTerritory(WorldRepositoryProvider provider, RuntimeTerritory territory) {
        if (provider.territories().exists(territory.id())) {
            provider.territories().changeRuler(territory.id(), territory.currentRulerId());
        } else {
            provider.territories().create(territory);
        }
    }

    private static void persistPlace(WorldRepositoryProvider provider, RuntimePlace place) {
        if (!provider.places().exists(place.id())) {
            provider.places().create(place);
        }
    }

    private static void persistStory(WorldRepositoryProvider provider, RuntimeStory story) {
        if (provider.stories().exists(story.id())) {
            story.linkedStories().forEach((linkedId, linkType) -> provider.stories().linkStory(story.id(), linkedId, linkType));
        } else {
            provider.stories().create(story);
        }
    }

    private static void persistRelationship(WorldRepositoryProvider provider, RuntimeRelationship relationship) {
        if (!provider.relationships().exists(relationship.id())) {
            provider.relationships().create(relationship);
        }
    }

    private static void persistOrganization(WorldRepositoryProvider provider, RuntimeOrganization organization) {
        if (!provider.organizations().exists(organization.id())) {
            provider.organizations().create(organization);
        } else {
            organization.roles().forEach((characterId, role) -> provider.organizations().assignRole(organization.id(), characterId, role));
        }
    }

    private static void persistResource(WorldRepositoryProvider provider, RuntimeResource resource) {
        if (!provider.resources().exists(resource.id())) {
            provider.resources().create(resource);
        }
    }

    private static void persistObject(WorldRepositoryProvider provider, RuntimeObject object) {
        if (!provider.objects().exists(object.id())) {
            provider.objects().create(object);
        }
    }

    private static void persistTimeline(WorldRepositoryProvider provider, RuntimeTimelineEntry entry) {
        if (!provider.timeline().exists(entry.id())) {
            provider.timeline().append(entry);
        }
    }

    private static void persistPromptProfile(WorldRepositoryProvider provider, RuntimePromptProfile profile) {
        if (provider.promptProfiles().exists(profile.id())) {
            provider.promptProfiles().update(profile);
        } else {
            provider.promptProfiles().create(profile);
        }
    }

    private static void persistCanon(WorldRepositoryProvider provider, RuntimeCanon canon) {
        if (!provider.canon().exists(canon.id())) {
            provider.canon().create(canon);
        }
    }

    private static void persistWorldRules(WorldRepositoryProvider provider, RuntimeWorldRules rules) {
        if (provider.worldRules().exists(rules.id())) {
            provider.worldRules().update(rules);
        } else {
            provider.worldRules().create(rules);
        }
    }

    private static void persistLaw(WorldRepositoryProvider provider, RuntimeLaw law) {
        if (!provider.laws().exists(law.id())) {
            provider.laws().create(law);
        }
    }

    private static void persistCustom(WorldRepositoryProvider provider, RuntimeCustom custom) {
        if (!provider.customs().exists(custom.id())) {
            provider.customs().create(custom);
        }
    }

    private static void persistGlossary(WorldRepositoryProvider provider, RuntimeGlossaryEntry entry) {
        if (!provider.glossary().exists(entry.id())) {
            provider.glossary().create(entry);
        }
    }
}
