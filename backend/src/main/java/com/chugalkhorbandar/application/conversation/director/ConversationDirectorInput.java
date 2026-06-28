package com.chugalkhorbandar.application.conversation.director;

import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationWindow;

public record ConversationDirectorInput(
        CurrentCharacter currentUser,
        WorkingMemory workingMemory,
        ConversationWindow conversationWindow,
        String latestUserMessage) {}
