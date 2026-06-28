package com.chugalkhorbandar.application.cognition;

import com.chugalkhorbandar.application.artifacts.ConversationArtifact;
import com.chugalkhorbandar.application.conversation.director.ConversationPlanSnapshot;
import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.Sender;
import java.util.List;
import java.util.stream.Collectors;

public final class CognitiveAnalysisPromptBuilder {

    private static final String SYSTEM_PROMPT =
            """
            You are a cognitive analysis engine for the Chugalkhor Bandar world.
            Analyze the conversation and return JSON only.
            Do not generate replies, prose, or explanations outside JSON.
            Use this exact schema:
            {
              "observations": [
                {
                  "type": "PREFERENCE",
                  "confidence": 0.94,
                  "summary": "Short descriptive observation.",
                  "evidence": "Quote or reference from transcript."
                }
              ],
              "recommendations": [
                {
                  "action": "WAIT",
                  "confidence": 0.91,
                  "reason": "Why this action is suggested.",
                  "target": "Optional target identifier."
                }
              ]
            }
            Observation types: PROMISE, REMINDER, PREFERENCE, RELATIONSHIP_SIGNAL, STORY_SEED, OPEN_QUESTION, GOSSIP, FACT_CANDIDATE, EMOTION, INTEREST, UNKNOWN.
            Recommendation actions: PROMOTE_TO_MEMORY, CREATE_NOTIFICATION, MERGE_ARTIFACT, IGNORE, WAIT.
            """;

    private CognitiveAnalysisPromptBuilder() {}

    public static String systemPrompt() {
        return SYSTEM_PROMPT;
    }

    public static String buildUserPayload(CognitiveAnalysisInput input) {
        StringBuilder payload = new StringBuilder();
        payload.append("Character: ").append(input.currentUser().displayName()).append('\n');
        payload.append("Conversation ID: ").append(input.conversation().conversationId()).append("\n\n");

        payload.append("Transcript:\n");
        for (ConversationMessage message : input.transcript()) {
            payload.append('-')
                    .append(message.sender() == Sender.USER ? "USER" : "BANDAR")
                    .append(": ")
                    .append(message.content())
                    .append('\n');
        }

        payload.append("\nArtifacts:\n");
        if (input.artifacts().isEmpty()) {
            payload.append("(none)\n");
        } else {
            for (ConversationArtifact artifact : input.artifacts()) {
                payload.append('-')
                        .append(artifact.type())
                        .append(" | ")
                        .append(artifact.summary())
                        .append('\n');
            }
        }

        payload.append("\nWorking Memory:\n");
        payload.append(summarizeWorkingMemory(input.workingMemory()));

        payload.append("\nBehavior Profile:\n");
        if (input.behaviorProfile() == null) {
            payload.append("(none)\n");
        } else {
            payload.append("flavor=")
                    .append(input.behaviorProfile().conversationFlavor())
                    .append(", humor=")
                    .append(input.behaviorProfile().humorLevel())
                    .append('\n');
        }

        payload.append("\nDirector Outcome:\n");
        ConversationPlanSnapshot plan = input.planSnapshot();
        if (plan == null) {
            payload.append("(none)\n");
        } else {
            payload.append("outcome=")
                    .append(plan.plan().outcome())
                    .append(", goal=")
                    .append(plan.plan().goal())
                    .append('\n');
        }

        if (!input.runtimeWorldSummary().isBlank()) {
            payload.append("\nRuntime World:\n").append(input.runtimeWorldSummary()).append('\n');
        }

        return payload.toString();
    }

    private static String summarizeWorkingMemory(WorkingMemory memory) {
        if (memory == null) {
            return "(none)\n";
        }
        return String.join(
                "\n",
                "topic=" + memory.activeTopic(),
                "mood=" + memory.conversationMood(),
                "story=" + memory.currentStory(),
                "entities=" + joinList(memory.activeEntities()),
                "questions=" + joinList(memory.unansweredQuestions()),
                "promises=" + joinList(memory.recentPromises()),
                "facts=" + joinList(memory.importantFacts()));
    }

    private static String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "(none)";
        }
        return values.stream().collect(Collectors.joining("; "));
    }
}
