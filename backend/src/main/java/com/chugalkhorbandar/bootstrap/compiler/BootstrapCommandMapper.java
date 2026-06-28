package com.chugalkhorbandar.bootstrap.compiler;

import com.chugalkhorbandar.bootstrap.compiler.command.*;
import com.chugalkhorbandar.bootstrap.typed.spec.*;
import java.util.LinkedHashMap;
import java.util.Map;

final class BootstrapCommandMapper {

    private BootstrapCommandMapper() {}

    static CreateCanonCommand toCanonCommand(CanonBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "canonicalFacts", spec.canonicalFacts());
        put(sections, "worldTruths", spec.worldTruths());
        put(sections, "stableRules", spec.stableRules());
        put(sections, "importantLoreReferences", spec.importantLoreReferences());
        return new CreateCanonCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateWorldRulesCommand toWorldRulesCommand(WorldRulesBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "whatIsCanon", spec.whatIsCanon());
        put(sections, "whatCanChange", spec.whatCanChange());
        put(sections, "contradictionRules", spec.contradictionRules());
        put(sections, "secrecyRules", spec.secrecyRules());
        put(sections, "preferencesRules", spec.preferencesRules());
        put(sections, "titleRules", spec.titleRules());
        put(sections, "deathRules", spec.deathRules());
        put(sections, "storyContinuityRules", spec.storyContinuityRules());
        return new CreateWorldRulesCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreatePromptProfileCommand toPromptProfileCommand(PromptProfileBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "identity", spec.identity());
        put(sections, "corePersonality", spec.corePersonality());
        put(sections, "speakingStyle", spec.speakingStyle());
        put(sections, "behavior", spec.behavior());
        put(sections, "forbiddenBehaviors", spec.forbiddenBehaviors());
        put(sections, "narrationRules", spec.narrationRules());
        return new CreatePromptProfileCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateTerritoryCommand toTerritoryCommand(TerritoryBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "territoryId", spec.territoryId());
        put(sections, "name", spec.name());
        put(sections, "capital", spec.capital());
        put(sections, "currentRuler", spec.currentRuler());
        put(sections, "government", spec.government());
        put(sections, "knownJungleCount", spec.knownJungleCount());
        put(sections, "history", spec.history());
        put(sections, "goals", spec.goals());
        put(sections, "notes", spec.notes());
        return new CreateTerritoryCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreatePlaceCommand toPlaceCommand(PlaceBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "placeId", spec.id());
        put(sections, "type", spec.type());
        put(sections, "description", spec.description());
        put(sections, "currentOwner", spec.currentOwner());
        put(sections, "locatedIn", spec.locatedIn());
        put(sections, "connectedPlaces", spec.connectedPlaces());
        put(sections, "importantLocations", spec.importantLocations());
        put(sections, "notes", spec.notes());
        return new CreatePlaceCommand(
                commandId(spec), executionOrder, spec.sourceDocumentId(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateOrganizationCommand toOrganizationCommand(OrganizationBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "organizationId", spec.organizationId());
        put(sections, "name", spec.name());
        put(sections, "type", spec.type());
        put(sections, "leader", spec.leader());
        put(sections, "headquarters", spec.headquarters());
        put(sections, "purpose", spec.purpose());
        put(sections, "knownMembers", spec.knownMembers());
        put(sections, "rules", spec.rules());
        put(sections, "notes", spec.notes());
        return new CreateOrganizationCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateResourceCommand toResourceCommand(ResourceBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "resourceId", spec.resourceId());
        put(sections, "name", spec.name());
        put(sections, "category", spec.category());
        put(sections, "description", spec.description());
        put(sections, "producedBy", spec.producedBy());
        put(sections, "lifecycle", spec.lifecycle());
        put(sections, "notes", spec.notes());
        return new CreateResourceCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateObjectCommand toObjectCommand(ObjectBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "objectId", spec.objectId());
        put(sections, "name", spec.name());
        put(sections, "type", spec.type());
        put(sections, "owner", spec.owner());
        put(sections, "location", spec.location());
        put(sections, "description", spec.description());
        put(sections, "history", spec.history());
        put(sections, "rules", spec.rules());
        put(sections, "notes", spec.notes());
        return new CreateObjectCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateCharacterCommand toCharacterCommand(CharacterBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "summary", spec.summary());
        put(sections, "basicInformation", spec.basicInformation());
        put(sections, "titles", spec.titles());
        put(sections, "roles", spec.roles());
        put(sections, "personality", spec.personality());
        put(sections, "history", spec.history());
        put(sections, "dailyRoutine", spec.dailyRoutine());
        put(sections, "relationships", spec.relationships());
        put(sections, "knownPreferences", spec.knownPreferences());
        put(sections, "abilities", spec.abilities());
        put(sections, "responsibilities", spec.responsibilities());
        put(sections, "assets", spec.assets());
        put(sections, "publicReputation", spec.publicReputation());
        put(sections, "secrets", spec.secrets());
        put(sections, "notes", spec.notes());
        return new CreateCharacterCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), spec.currentPlace(), spec.homeTerritory(), metadata(spec));
    }

    static CreateRelationshipCommand toRelationshipCommand(RelationshipBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "relationshipType", spec.relationshipType());
        put(sections, "characters", spec.characters());
        put(sections, "description", spec.description());
        put(sections, "relationshipStatus", spec.relationshipStatus());
        return new CreateRelationshipCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateStoryCommand toStoryCommand(StoryBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "summary", spec.summary());
        put(sections, "participants", spec.participants());
        put(sections, "majorPlaces", spec.majorPlaces());
        put(sections, "beginning", spec.beginning());
        put(sections, "keyEvents", spec.keyEvents());
        put(sections, "ending", spec.ending());
        put(sections, "canonicalConsequences", spec.canonicalConsequences());
        put(sections, "linkedCharacters", spec.linkedCharacters());
        put(sections, "linkedPlaces", spec.linkedPlaces());
        put(sections, "linkedOrganizations", spec.linkedOrganizations());
        put(sections, "linkedStories", spec.linkedStories());
        put(sections, "notes", spec.notes());
        return new CreateStoryCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateChronologyCommand toChronologyCommand(ChronologyBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        return new CreateChronologyCommand(
                commandId(spec),
                executionOrder,
                spec.id(),
                spec.sourcePath(),
                spec.id(),
                spec.title(),
                spec.timelineItems(),
                finalize(sections),
                metadata(spec));
    }

    static CreateLawCommand toLawCommand(LawBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "lawNumber", spec.lawNumber());
        put(sections, "lawTitle", spec.lawTitle());
        put(sections, "description", spec.description());
        return new CreateLawCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateCustomCommand toCustomCommand(CustomBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "category", spec.category());
        put(sections, "customTitle", spec.customTitle());
        put(sections, "description", spec.description());
        return new CreateCustomCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    static CreateGlossaryEntryCommand toGlossaryEntryCommand(GlossaryEntryBootstrapSpec spec, int executionOrder) {
        validateSpec(spec);
        Map<String, String> sections = buildSections(spec.unmappedSections());
        put(sections, "term", spec.term());
        put(sections, "definition", spec.definition());
        put(sections, "references", spec.references());
        return new CreateGlossaryEntryCommand(
                commandId(spec), executionOrder, spec.id(), spec.sourcePath(), spec.id(), spec.title(), finalize(sections), metadata(spec));
    }

    private static void validateSpec(BootstrapTypedSpec spec) {
        if (spec == null) {
            throw new BootstrapCompilationException("Cannot compile null bootstrap spec");
        }
        if (spec.id() == null || spec.id().isBlank()) {
            throw new BootstrapCompilationException("Bootstrap spec is missing source document id");
        }
    }

    private static String commandId(BootstrapTypedSpec spec) {
        return spec.id();
    }

    private static Map<String, String> metadata(BootstrapTypedSpec spec) {
        return CommandMetadataSupport.metadata(spec);
    }

    private static Map<String, String> buildSections(Map<String, String> unmappedSections) {
        Map<String, String> sections = new LinkedHashMap<>();
        if (unmappedSections != null) {
            sections.putAll(unmappedSections);
        }
        return sections;
    }

    private static void put(Map<String, String> sections, String key, String value) {
        if (value != null) {
            sections.put(key, value);
        }
    }

    private static Map<String, String> finalize(Map<String, String> sections) {
        return Map.copyOf(sections);
    }
}
