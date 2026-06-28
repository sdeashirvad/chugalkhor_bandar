package com.chugalkhorbandar.adapters.api.dto;

import java.time.Instant;
import java.util.Map;

public record ConversationMessageDto(
        String messageId,
        String sender,
        Instant timestamp,
        String content,
        String visibility,
        Map<String, String> metadata) {}
