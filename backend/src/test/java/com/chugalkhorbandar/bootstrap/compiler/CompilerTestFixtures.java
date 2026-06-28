package com.chugalkhorbandar.bootstrap.compiler;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.BootstrapTypedWorld;
import com.chugalkhorbandar.bootstrap.typed.spec.*;
import java.nio.file.Path;
import java.util.Map;

public final class CompilerTestFixtures {

    private CompilerTestFixtures() {}

    public static BootstrapTypedWorld sampleWorld() {
        Path source = Path.of("bootstrap/test.md");
        return BootstrapTypedWorld.builder()
                .addCanon(new CanonBootstrapSpec(
                        "canon", "Canon", source, "ACTIVE", "1.0", DocumentType.CANON,
                        "facts", null, null, null, Map.of()))
                .addWorldRules(new WorldRulesBootstrapSpec(
                        "world-rules", "World Rules", source, "ACTIVE", "1.0", DocumentType.WORLD_RULES,
                        "canon rules", null, null, null, null, null, null, null, Map.of()))
                .addPromptProfile(new PromptProfileBootstrapSpec(
                        "prompt_guide", "Guide", source, "ACTIVE", "1.0", DocumentType.PROMPT,
                        "identity text", null, null, null, null, null, Map.of()))
                .addTerritory(new TerritoryBootstrapSpec(
                        "territories", "Territories", source, "ACTIVE", "1.0", DocumentType.TERRITORIES,
                        null, null, null, null, null, null, null, null, null, Map.of("Home", "jungle")))
                .addPlace(new PlaceBootstrapSpec(
                        "place_home", "Home Place", source, "ACTIVE", "1.0", DocumentType.PLACES,
                        "places", "Capital Jungle", "desc", null, "Home Jungle", null, null, null, Map.of()))
                .addOrganization(new OrganizationBootstrapSpec(
                        "organizations", "Organizations", source, "ACTIVE", "1.0", DocumentType.ORGANIZATIONS,
                        null, null, null, null, null, null, null, null, null, Map.of()))
                .addResource(new ResourceBootstrapSpec(
                        "resources", "Resources", source, "ACTIVE", "1.0", DocumentType.RESOURCES,
                        null, null, null, null, null, null, null, Map.of()))
                .addObject(new ObjectBootstrapSpec(
                        "objects", "Objects", source, "ACTIVE", "1.0", DocumentType.OBJECTS,
                        null, null, null, null, null, null, null, null, null, Map.of()))
                .addCharacter(new CharacterBootstrapSpec(
                        "character_zeta", "Zeta", source, "ACTIVE", "1.0", DocumentType.CHARACTER,
                        "summary", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, Map.of()))
                .addCharacter(new CharacterBootstrapSpec(
                        "character_alpha", "Alpha", source, "ACTIVE", "1.0", DocumentType.CHARACTER,
                        "summary", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "place_home", null, Map.of()))
                .addRelationship(new RelationshipBootstrapSpec(
                        "relationships", "Relationships", source, "ACTIVE", "1.0", DocumentType.RELATIONSHIPS,
                        null, null, null, null, Map.of()))
                .addStory(new StoryBootstrapSpec(
                        "story_origin", "Origin", source, "ACTIVE", "1.0", DocumentType.STORY,
                        "summary", null, null, null, null, null, null, null, null, null, null, null, Map.of()))
                .addChronology(new ChronologyBootstrapSpec(
                        "world_timeline", "Timeline", source, "ACTIVE", "1.0", DocumentType.CHRONOLOGY,
                        java.util.List.of(), Map.of("Ancient Era", "events")))
                .addLaw(new LawBootstrapSpec(
                        "laws", "Laws", source, "ACTIVE", "1.0", DocumentType.LAWS,
                        null, null, null, Map.of("Law 1", "rule")))
                .addCustom(new CustomBootstrapSpec(
                        "customs", "Customs", source, "ACTIVE", "1.0", DocumentType.CUSTOMS,
                        null, null, null, Map.of()))
                .addGlossaryEntry(new GlossaryEntryBootstrapSpec(
                        "glossary", "Glossary", source, "ACTIVE", "1.0", DocumentType.GLOSSARY,
                        null, null, null, Map.of("Term", "definition")))
                .build();
    }

    public static BootstrapTypedWorld worldWithDuplicateIds() {
        Path source = Path.of("bootstrap/dup.md");
        CharacterBootstrapSpec first = new CharacterBootstrapSpec(
                "duplicate_id", "First", source, "ACTIVE", "1.0", DocumentType.CHARACTER,
                "summary", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, Map.of());
        CharacterBootstrapSpec second = new CharacterBootstrapSpec(
                "duplicate_id", "Second", source, "ACTIVE", "1.0", DocumentType.CHARACTER,
                "summary", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, Map.of());
        return BootstrapTypedWorld.builder().addCharacter(first).addCharacter(second).build();
    }
}
