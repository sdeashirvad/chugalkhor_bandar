package com.chugalkhorbandar.application.context;

import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.Conversation;

public record ContextPlannerRequest(
        CurrentCharacter currentCharacter,
        ChatSession session,
        Conversation currentConversation,
        String latestUserMessage,
        RuntimeWorldContext runtimeWorld) {}
