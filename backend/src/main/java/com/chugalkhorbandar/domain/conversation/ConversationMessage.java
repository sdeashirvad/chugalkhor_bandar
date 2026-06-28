package com.chugalkhorbandar.domain.conversation;

import java.time.Instant;
import java.util.Map;

public record ConversationMessage(
        String messageId,
        String conversationId,
        Sender sender,
        Instant timestamp,
        String content,
        Visibility visibility,
        Map<String, String> metadata) {}
