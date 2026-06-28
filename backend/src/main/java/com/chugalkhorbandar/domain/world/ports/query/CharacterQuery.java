package com.chugalkhorbandar.domain.world.ports.query;

public record CharacterQuery(String placeId, String titleContains) {

    public static CharacterQuery all() {
        return new CharacterQuery(null, null);
    }

    public static CharacterQuery atPlace(String placeId) {
        return new CharacterQuery(placeId, null);
    }

    public static CharacterQuery withTitleContaining(String titleContains) {
        return new CharacterQuery(null, titleContains);
    }
}
