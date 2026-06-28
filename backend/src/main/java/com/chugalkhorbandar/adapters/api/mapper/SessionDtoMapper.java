package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.CurrentCharacterDto;
import com.chugalkhorbandar.adapters.api.dto.SessionResponseDto;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;

public final class SessionDtoMapper {

    private SessionDtoMapper() {}

    public static SessionResponseDto toDto(ChatSession session) {
        return new SessionResponseDto(
                session.sessionId(),
                toDto(session.currentCharacter()),
                session.startedAt(),
                session.lastActivity(),
                session.status().name());
    }

    public static CurrentCharacterDto toDto(CurrentCharacter character) {
        return new CurrentCharacterDto(
                character.id(),
                character.displayName(),
                character.titles(),
                character.species(),
                character.homeTerritory(),
                character.currentLocation());
    }
}
