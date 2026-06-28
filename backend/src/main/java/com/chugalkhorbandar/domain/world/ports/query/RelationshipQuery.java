package com.chugalkhorbandar.domain.world.ports.query;

public record RelationshipQuery(String characterId, String relationshipType) {

    public static RelationshipQuery all() {
        return new RelationshipQuery(null, null);
    }

    public static RelationshipQuery forCharacter(String characterId) {
        return new RelationshipQuery(characterId, null);
    }

    public static RelationshipQuery ofType(String relationshipType) {
        return new RelationshipQuery(null, relationshipType);
    }
}
