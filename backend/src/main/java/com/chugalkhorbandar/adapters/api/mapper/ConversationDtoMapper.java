package com.chugalkhorbandar.adapters.api.mapper;

import com.chugalkhorbandar.adapters.api.dto.ConversationCharacterDto;
import com.chugalkhorbandar.adapters.api.dto.ConversationMessageDto;
import com.chugalkhorbandar.adapters.api.dto.ConversationResponseDto;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import java.util.List;

public final class ConversationDtoMapper {

    private ConversationDtoMapper() {}

    public static ConversationResponseDto toDto(Conversation conversation) {
        return new ConversationResponseDto(
                conversation.conversationId(),
                conversation.sessionId(),
                toDto(conversation.currentCharacter()),
                conversation.startedAt(),
                conversation.lastActivity(),
                conversation.status().name());
    }

    public static ConversationCharacterDto toDto(ConversationCharacter character) {
        return new ConversationCharacterDto(
                character.id(),
                character.displayName(),
                character.titles(),
                character.species(),
                character.homeTerritory());
    }

    public static ConversationMessageDto toDto(ConversationMessage message) {
        return new ConversationMessageDto(
                message.messageId(),
                message.sender().name(),
                message.timestamp(),
                message.content(),
                message.visibility().name(),
                message.metadata());
    }

    public static List<ConversationMessageDto> toDtos(List<ConversationMessage> messages) {
        return messages.stream().map(ConversationDtoMapper::toDto).toList();
    }
}
