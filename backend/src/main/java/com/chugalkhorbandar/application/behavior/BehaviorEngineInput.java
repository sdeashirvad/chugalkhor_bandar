package com.chugalkhorbandar.application.behavior;

import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.ConversationWindow;

public record BehaviorEngineInput(
        CurrentCharacter currentUser,
        WorkingMemory workingMemory,
        ConversationPlan conversationPlan,
        ConversationWindow conversationWindow,
        RuntimeWorldContext runtimeWorld,
        String latestUserMessage) {}
