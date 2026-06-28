package com.chugalkhorbandar.application.session;

import java.util.List;

public record CurrentCharacter(
        String id, String displayName, List<String> titles, String species, String homeTerritory, String currentLocation) {}
