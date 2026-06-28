package com.chugalkhorbandar.domain.conversation;

import java.util.List;

public record ConversationWindow(
        List<ConversationMessage> messages, Sender currentTurn, int tokenEstimate) {}
