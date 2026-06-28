package com.chugalkhorbandar.application.prompt;

import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.Conversation;

public record PromptComposeRequest(
        ResolvedContext resolvedContext,
        String latestUserMessage,
        CurrentCharacter currentCharacter,
        ChatSession session,
        Conversation conversation) {}
