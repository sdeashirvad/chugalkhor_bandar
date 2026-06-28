package com.chugalkhorbandar.application.cognition;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.behavior.BehaviorProfile;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import java.util.List;

public record CognitiveAnalysisInput(
        CurrentCharacter currentUser,
        Conversation conversation,
        List<ConversationMessage> transcript,
        List<ConversationArtifact> artifacts,
        WorkingMemory workingMemory,
        BehaviorProfile behaviorProfile,
        ConversationPlanSnapshot planSnapshot,
        String runtimeWorldSummary) {

    public CognitiveAnalysisInput {
        transcript = List.copyOf(transcript == null ? List.of() : transcript);
        artifacts = List.copyOf(artifacts == null ? List.of() : artifacts);
        runtimeWorldSummary = runtimeWorldSummary == null ? "" : runtimeWorldSummary;
    }
}
