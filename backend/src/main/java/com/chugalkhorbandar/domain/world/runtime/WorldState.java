package com.chugalkhorbandar.domain.world.runtime;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public record WorldState(
        Map<String, RuntimeCharacter> characters,
        Map<String, RuntimeTerritory> territories,
        Map<String, RuntimePlace> places,
        Map<String, RuntimeOrganization> organizations,
        Map<String, RuntimeResource> resources,
        Map<String, RuntimeObject> objects,
        Map<String, RuntimeRelationship> relationships,
        Map<String, RuntimeStory> stories,
        Map<String, RuntimeTimelineEntry> timeline,
        Map<String, RuntimePromptProfile> promptProfiles,
        Map<String, RuntimeWorldRules> worldRules,
        Map<String, RuntimeCanon> canon,
        Map<String, RuntimeGlossaryEntry> glossary,
        Map<String, RuntimeLaw> laws,
        Map<String, RuntimeCustom> customs) {

    public WorldState {
        characters = Map.copyOf(characters);
        territories = Map.copyOf(territories);
        places = Map.copyOf(places);
        organizations = Map.copyOf(organizations);
        resources = Map.copyOf(resources);
        objects = Map.copyOf(objects);
        relationships = Map.copyOf(relationships);
        stories = Map.copyOf(stories);
        timeline = Map.copyOf(timeline);
        promptProfiles = Map.copyOf(promptProfiles);
        worldRules = Map.copyOf(worldRules);
        canon = Map.copyOf(canon);
        glossary = Map.copyOf(glossary);
        laws = Map.copyOf(laws);
        customs = Map.copyOf(customs);
    }

    public static WorldState empty() {
        return new WorldState(
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of());
    }

    public WorldState addCharacter(RuntimeCharacter character) {
        return new WorldState(
                putUnique(characters, character.id(), character, "Characters"),
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState updateCharacter(String characterId, Function<RuntimeCharacter, RuntimeCharacter> updater) {
        return new WorldState(
                replace(characters, characterId, updater, "Characters"),
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState removeCharacter(String characterId) {
        return new WorldState(
                remove(characters, characterId, "Characters"),
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addTerritory(RuntimeTerritory territory) {
        return new WorldState(
                characters,
                putUnique(territories, territory.id(), territory, "Territories"),
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState updateTerritory(String territoryId, Function<RuntimeTerritory, RuntimeTerritory> updater) {
        return new WorldState(
                characters,
                replace(territories, territoryId, updater, "Territories"),
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addPlace(RuntimePlace place) {
        return new WorldState(
                characters,
                territories,
                putUnique(places, place.id(), place, "Places"),
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addOrganization(RuntimeOrganization organization) {
        return new WorldState(
                characters,
                territories,
                places,
                putUnique(organizations, organization.id(), organization, "Organizations"),
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState updateOrganization(
            String organizationId, Function<RuntimeOrganization, RuntimeOrganization> updater) {
        return new WorldState(
                characters,
                territories,
                places,
                replace(organizations, organizationId, updater, "Organizations"),
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addResource(RuntimeResource resource) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                putUnique(resources, resource.id(), resource, "Resources"),
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState updateResource(String resourceId, Function<RuntimeResource, RuntimeResource> updater) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                replace(resources, resourceId, updater, "Resources"),
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addObject(RuntimeObject object) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                putUnique(objects, object.id(), object, "Objects"),
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState updateObject(String objectId, Function<RuntimeObject, RuntimeObject> updater) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                replace(objects, objectId, updater, "Objects"),
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addRelationship(RuntimeRelationship relationship) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                putUnique(relationships, relationship.id(), relationship, "Relationships"),
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState removeRelationship(String relationshipId) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                remove(relationships, relationshipId, "Relationships"),
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addStory(RuntimeStory story) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                putUnique(stories, story.id(), story, "Stories"),
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState updateStory(String storyId, Function<RuntimeStory, RuntimeStory> updater) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                replace(stories, storyId, updater, "Stories"),
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addTimelineEntry(RuntimeTimelineEntry entry) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                putUnique(timeline, entry.id(), entry, "Timeline"),
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addPromptProfile(RuntimePromptProfile profile) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                putUnique(promptProfiles, profile.id(), profile, "Prompt Profiles"),
                worldRules,
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addWorldRules(RuntimeWorldRules rules) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                putUnique(worldRules, rules.id(), rules, "World Rules"),
                canon,
                glossary,
                laws,
                customs);
    }

    public WorldState addCanon(RuntimeCanon canonEntry) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                putUnique(canon, canonEntry.id(), canonEntry, "Canon"),
                glossary,
                laws,
                customs);
    }

    public WorldState addGlossaryEntry(RuntimeGlossaryEntry entry) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                putUnique(glossary, entry.id(), entry, "Glossary"),
                laws,
                customs);
    }

    public WorldState addLaw(RuntimeLaw law) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                putUnique(laws, law.id(), law, "Laws"),
                customs);
    }

    public WorldState addCustom(RuntimeCustom custom) {
        return new WorldState(
                characters,
                territories,
                places,
                organizations,
                resources,
                objects,
                relationships,
                stories,
                timeline,
                promptProfiles,
                worldRules,
                canon,
                glossary,
                laws,
                putUnique(customs, custom.id(), custom, "Customs"));
    }

    public Map<String, Integer> statistics() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Characters", characters.size());
        stats.put("Territories", territories.size());
        stats.put("Places", places.size());
        stats.put("Organizations", organizations.size());
        stats.put("Resources", resources.size());
        stats.put("Objects", objects.size());
        stats.put("Relationships", relationships.size());
        stats.put("Stories", stories.size());
        stats.put("Timeline Entries", timeline.size());
        stats.put("Prompt Profiles", promptProfiles.size());
        stats.put("World Rules", worldRules.size());
        stats.put("Canon", canon.size());
        stats.put("Glossary", glossary.size());
        stats.put("Laws", laws.size());
        stats.put("Customs", customs.size());
        return Map.copyOf(stats);
    }

    private static <T> Map<String, T> putUnique(
            Map<String, T> current, String id, T value, String collectionName) {
        HandlerSupport.ensureUnique(current, id, collectionName);
        Map<String, T> updated = new LinkedHashMap<>(current);
        updated.put(id, value);
        return Map.copyOf(updated);
    }

    private static <T> Map<String, T> replace(
            Map<String, T> current, String id, Function<T, T> updater, String collectionName) {
        T existing = HandlerSupport.requirePresent(current, id, collectionName);
        Map<String, T> updated = new LinkedHashMap<>(current);
        updated.put(id, updater.apply(existing));
        return Map.copyOf(updated);
    }

    private static <T> Map<String, T> remove(Map<String, T> current, String id, String collectionName) {
        HandlerSupport.requirePresent(current, id, collectionName);
        Map<String, T> updated = new LinkedHashMap<>(current);
        updated.remove(id);
        return Map.copyOf(updated);
    }
}
