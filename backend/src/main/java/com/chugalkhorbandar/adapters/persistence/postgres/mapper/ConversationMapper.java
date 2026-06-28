package com.chugalkhorbandar.adapters.persistence.postgres.mapper;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ConversationEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.entity.ConversationMessageEntity;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.ConversationStatus;
import com.chugalkhorbandar.domain.conversation.Sender;
import com.chugalkhorbandar.domain.conversation.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ConversationMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {};
    private static final TypeReference<Map<String, String>> STRING_MAP = new TypeReference<>() {};

    private ConversationMapper() {}

    public static ConversationEntity toEntity(Conversation conversation) {
        ConversationEntity entity = new ConversationEntity();
        entity.setConversationId(conversation.conversationId());
        entity.setSessionId(conversation.sessionId());
        entity.setCharacterId(conversation.currentCharacter().id());
        entity.setCharacterDisplayName(conversation.currentCharacter().displayName());
        entity.setCharacterTitles(writeJson(conversation.currentCharacter().titles()));
        entity.setCharacterSpecies(conversation.currentCharacter().species());
        entity.setCharacterHomeTerritory(conversation.currentCharacter().homeTerritory());
        entity.setStartedAt(conversation.startedAt());
        entity.setLastActivity(conversation.lastActivity());
        entity.setStatus(conversation.status().name());
        return entity;
    }

    public static Conversation toDomain(ConversationEntity entity, List<ConversationMessage> messages) {
        return new Conversation(
                entity.getConversationId(),
                entity.getSessionId(),
                new ConversationCharacter(
                        entity.getCharacterId(),
                        entity.getCharacterDisplayName(),
                        readStringList(entity.getCharacterTitles()),
                        entity.getCharacterSpecies(),
                        entity.getCharacterHomeTerritory()),
                entity.getStartedAt(),
                entity.getLastActivity(),
                ConversationStatus.valueOf(entity.getStatus()),
                messages);
    }

    public static ConversationMessageEntity toEntity(ConversationMessage message, int sequenceOrder) {
        ConversationMessageEntity entity = new ConversationMessageEntity();
        entity.setMessageId(message.messageId());
        entity.setConversationId(message.conversationId());
        entity.setSender(message.sender().name());
        entity.setMessageTimestamp(message.timestamp());
        entity.setContent(message.content());
        entity.setVisibility(message.visibility().name());
        entity.setMetadata(writeJson(message.metadata()));
        entity.setSequenceOrder(sequenceOrder);
        return entity;
    }

    public static ConversationMessage toDomain(ConversationMessageEntity entity) {
        return new ConversationMessage(
                entity.getMessageId(),
                entity.getConversationId(),
                Sender.valueOf(entity.getSender()),
                entity.getMessageTimestamp(),
                entity.getContent(),
                Visibility.valueOf(entity.getVisibility()),
                readStringMap(entity.getMetadata()));
    }

    private static String writeJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize conversation JSON", exception);
        }
    }

    private static List<String> readStringList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return OBJECT_MAPPER.readValue(json, STRING_LIST);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize conversation titles", exception);
        }
    }

    private static Map<String, String> readStringMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return OBJECT_MAPPER.readValue(json, STRING_MAP);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize conversation metadata", exception);
        }
    }
}
