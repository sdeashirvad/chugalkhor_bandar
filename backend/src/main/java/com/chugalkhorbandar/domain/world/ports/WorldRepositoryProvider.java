package com.chugalkhorbandar.domain.world.ports;

public interface WorldRepositoryProvider {

    CharacterRepository characters();

    TerritoryRepository territories();

    PlaceRepository places();

    StoryRepository stories();

    RelationshipRepository relationships();

    OrganizationRepository organizations();

    ResourceRepository resources();

    ObjectRepository objects();

    TimelineRepository timeline();

    PromptProfileRepository promptProfiles();

    CanonRepository canon();

    WorldRulesRepository worldRules();

    LawRepository laws();

    CustomRepository customs();

    GlossaryRepository glossary();
}
