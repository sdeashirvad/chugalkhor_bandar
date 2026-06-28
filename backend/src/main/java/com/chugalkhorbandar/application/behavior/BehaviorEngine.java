package com.chugalkhorbandar.application.behavior;

import com.chugalkhorbandar.application.conversation.director.ConversationArc;
import com.chugalkhorbandar.application.conversation.director.ConversationEnergy;
import com.chugalkhorbandar.application.conversation.director.ConversationGoal;
import com.chugalkhorbandar.application.conversation.director.ConversationPlan;
import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class BehaviorEngine {

    public BehaviorProfileSnapshot select(String sessionId, BehaviorEngineInput input) {
        ConversationPlan plan = input.conversationPlan();
        List<BehaviorPlanningTraceEntry> trace = new ArrayList<>();
        String normalized = normalize(input.latestUserMessage());
        int hash = BehaviorDeterministicHash.hash(sessionId, input.latestUserMessage(), "behavior");

        BehaviorProfile profile =
                switch (plan.goal()) {
                    case STORY, CONTINUE_STORY -> storyBehavior(plan, normalized, hash, trace);
                    case LOCATION_HELP -> locationBehavior(hash, trace);
                    case CHEER_UP -> cheerUpBehavior(hash, trace);
                    case GOODBYE -> goodbyeBehavior(trace);
                    case GREETING -> greetingBehavior(hash, trace);
                    case IDENTITY -> identityBehavior(trace);
                    case REMEMBER, REMINDER -> reminderBehavior(trace);
                    case QUESTION -> questionBehavior(plan, normalized, hash, trace);
                    default -> defaultBehavior(plan, hash, trace);
                };

        applyWorldAndMemoryAdjustments(input.workingMemory(), input.runtimeWorld(), profile, trace);
        return new BehaviorProfileSnapshot(sessionId, profile, new BehaviorPlanningTrace(trace));
    }

    private BehaviorProfile storyBehavior(
            ConversationPlan plan, String normalized, int hash, List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("story-behavior", "Conversation goal is storytelling"));
        ConversationFlavor flavor = pickStoryFlavor(plan, normalized, hash);
        trace.add(new BehaviorPlanningTraceEntry(
                "story-flavor", "Selected " + flavor + " flavor for narrative mood"));
        return profile(
                OpeningStyle.MEMORY,
                NarrationStyle.STORY,
                HumorLevel.LIGHT,
                CuriosityLevel.HIGH,
                plan.askFollowUpQuestion() ? EndingStyle.QUESTION : EndingStyle.REFLECTION,
                flavor,
                EnergyModifier.LIVELY,
                StorytellingPreference.STRONG,
                trace);
    }

    private BehaviorProfile locationBehavior(int hash, List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("location-behavior", "Conversation goal is location help"));
        HumorLevel humor = hash % 2 == 0 ? HumorLevel.OFF : HumorLevel.LIGHT;
        trace.add(new BehaviorPlanningTraceEntry(
                "location-humor", humor == HumorLevel.OFF ? "Humor off for clarity" : "Light humor allowed"));
        return profile(
                OpeningStyle.DIRECT,
                NarrationStyle.DIRECT,
                humor,
                CuriosityLevel.LOW,
                EndingStyle.NONE,
                ConversationFlavor.CALM,
                EnergyModifier.STEADY,
                StorytellingPreference.MINIMAL,
                trace);
    }

    private BehaviorProfile cheerUpBehavior(int hash, List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("cheer-up-behavior", "Conversation goal is cheer up"));
        OpeningStyle opening = hash % 2 == 0 ? OpeningStyle.JOKE : OpeningStyle.OBSERVATION;
        trace.add(new BehaviorPlanningTraceEntry(
                "cheer-up-opening", opening == OpeningStyle.JOKE ? "Open playfully" : "Open with observation"));
        return profile(
                opening,
                NarrationStyle.PLAYFUL,
                HumorLevel.MEDIUM,
                CuriosityLevel.MEDIUM,
                EndingStyle.QUESTION,
                ConversationFlavor.COZY,
                EnergyModifier.LIVELY,
                StorytellingPreference.BALANCED,
                trace);
    }

    private BehaviorProfile goodbyeBehavior(List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("goodbye-behavior", "Conversation goal is goodbye"));
        return profile(
                OpeningStyle.DIRECT,
                NarrationStyle.DIRECT,
                HumorLevel.OFF,
                CuriosityLevel.LOW,
                EndingStyle.NONE,
                ConversationFlavor.CALM,
                EnergyModifier.SUBDUED,
                StorytellingPreference.MINIMAL,
                trace);
    }

    private BehaviorProfile greetingBehavior(int hash, List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("greeting-behavior", "Conversation goal is greeting"));
        ConversationFlavor flavor = hash % 2 == 0 ? ConversationFlavor.COZY : ConversationFlavor.CURIOUS;
        return profile(
                OpeningStyle.GREETING,
                NarrationStyle.DIRECT,
                HumorLevel.LIGHT,
                CuriosityLevel.MEDIUM,
                EndingStyle.INVITATION,
                flavor,
                EnergyModifier.STEADY,
                StorytellingPreference.MINIMAL,
                trace);
    }

    private BehaviorProfile identityBehavior(List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("identity-behavior", "Conversation goal is identity"));
        return profile(
                OpeningStyle.DIRECT,
                NarrationStyle.DIRECT,
                HumorLevel.OFF,
                CuriosityLevel.LOW,
                EndingStyle.NONE,
                ConversationFlavor.CALM,
                EnergyModifier.STEADY,
                StorytellingPreference.MINIMAL,
                trace);
    }

    private BehaviorProfile reminderBehavior(List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("reminder-behavior", "Conversation goal is remember or reminder"));
        return profile(
                OpeningStyle.DIRECT,
                NarrationStyle.DIRECT,
                HumorLevel.OFF,
                CuriosityLevel.LOW,
                EndingStyle.PROMISE,
                ConversationFlavor.CALM,
                EnergyModifier.STEADY,
                StorytellingPreference.MINIMAL,
                trace);
    }

    private BehaviorProfile questionBehavior(
            ConversationPlan plan, String normalized, int hash, List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("question-behavior", "Conversation goal is question"));
        if (normalized.contains("history") || normalized.contains("historical")) {
            trace.add(new BehaviorPlanningTraceEntry("historical-question", "Historical topic detected"));
            return profile(
                    OpeningStyle.OBSERVATION,
                    NarrationStyle.HISTORICAL,
                    HumorLevel.LIGHT,
                    CuriosityLevel.MEDIUM,
                    EndingStyle.REFLECTION,
                    ConversationFlavor.NOSTALGIC,
                    EnergyModifier.LIVELY,
                    StorytellingPreference.BALANCED,
                    trace);
        }
        NarrationStyle narration = plan.conversationEnergy() == ConversationEnergy.VERY_HIGH
                ? NarrationStyle.HISTORICAL
                : NarrationStyle.DIRECT;
        return profile(
                OpeningStyle.DIRECT,
                narration,
                HumorLevel.LIGHT,
                CuriosityLevel.MEDIUM,
                plan.askFollowUpQuestion() ? EndingStyle.QUESTION : EndingStyle.REFLECTION,
                ConversationFlavor.CURIOUS,
                EnergyModifier.STEADY,
                StorytellingPreference.MINIMAL,
                trace);
    }

    private BehaviorProfile defaultBehavior(
            ConversationPlan plan, int hash, List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("default-behavior", "No specialized rule matched; using balanced style"));
        if (plan.conversationArc() == ConversationArc.CHEER_UP) {
            return cheerUpBehavior(hash, trace);
        }
        if (plan.tellStory()) {
            return storyBehavior(plan, normalize(""), hash, trace);
        }
        return profile(
                OpeningStyle.OBSERVATION,
                NarrationStyle.DIRECT,
                HumorLevel.LIGHT,
                CuriosityLevel.MEDIUM,
                EndingStyle.INVITATION,
                ConversationFlavor.CURIOUS,
                EnergyModifier.STEADY,
                StorytellingPreference.MINIMAL,
                trace);
    }

    private void applyWorldAndMemoryAdjustments(
            WorkingMemory memory,
            com.chugalkhorbandar.application.context.RuntimeWorldContext runtimeWorld,
            BehaviorProfile profile,
            List<BehaviorPlanningTraceEntry> trace) {
        if (runtimeWorld != null && runtimeWorld.storyCount() > 0 && profile.storytellingPreference() == StorytellingPreference.MINIMAL) {
            trace.add(new BehaviorPlanningTraceEntry(
                    "world-stories-available", "Runtime world has stories; storytelling preference unchanged"));
        }
        if (memory != null
                && memory.currentStory() != null
                && !memory.currentStory().isBlank()
                && profile.narrationStyle() != NarrationStyle.STORY) {
            trace.add(new BehaviorPlanningTraceEntry(
                    "memory-story-in-progress", "Working memory indicates story momentum"));
        }
    }

    private static ConversationFlavor pickStoryFlavor(ConversationPlan plan, String normalized, int hash) {
        if (plan.conversationArc() == ConversationArc.QUESTION_STORY
                && (normalized.contains("festival") || normalized.contains("celebration"))) {
            return ConversationFlavor.CELEBRATORY;
        }
        if (normalized.contains("festival") || normalized.contains("celebration")) {
            return ConversationFlavor.CELEBRATORY;
        }
        return hash % 2 == 0 ? ConversationFlavor.NOSTALGIC : ConversationFlavor.ADVENTUROUS;
    }

    private static BehaviorProfile profile(
            OpeningStyle opening,
            NarrationStyle narration,
            HumorLevel humor,
            CuriosityLevel curiosity,
            EndingStyle ending,
            ConversationFlavor flavor,
            EnergyModifier energy,
            StorytellingPreference storytelling,
            List<BehaviorPlanningTraceEntry> trace) {
        trace.add(new BehaviorPlanningTraceEntry("behavior-assembled", "Behavior profile assembled from matched rules"));
        return new BehaviorProfile(
                opening, narration, humor, curiosity, ending, flavor, energy, storytelling, Instant.now());
    }

    private static String normalize(String message) {
        return message == null ? "" : message.toLowerCase(Locale.ROOT).trim();
    }
}
