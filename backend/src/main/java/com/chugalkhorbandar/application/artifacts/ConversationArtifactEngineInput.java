package com.chugalkhorbandar.application.artifacts;

import com.chugalkhorbandar.application.behavior.BehaviorProfile;
import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.Conversation;
import java.time.Instant;
import java.util.List;

public record ConversationArtifactEngineInput(
        CurrentCharacter currentUser,
        Conversation conversation,
        WorkingMemory workingMemory,
        ConversationPlanSnapshot planSnapshot,
        BehaviorProfile behaviorProfile,
        RuntimeWorldContext runtimeWorld,
        String latestUserMessage,
        Instant currentTime,
        List<ConversationArtifact> existingArtifacts) {

    public ConversationArtifactEngineInput {
        existingArtifacts = List.copyOf(existingArtifacts == null ? List.of() : existingArtifacts);
    }
}
