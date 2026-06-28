package com.chugalkhorbandar.domain.conversation;

import java.util.List;

public record ConversationCharacter(
        String id, String displayName, List<String> titles, String species, String homeTerritory) {}
