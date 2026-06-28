package com.chugalkhorbandar.application.conversation.director;

import com.chugalkhorbandar.application.memory.working.WorkingMemory;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.Sender;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ConversationDirector {

    private final ConversationDirectorProperties properties;

    public ConversationDirector(ConversationDirectorProperties properties) {
        this.properties = properties;
    }

    public ConversationPlanSnapshot plan(String sessionId, ConversationDirectorInput input) {
        String normalized = normalize(input.latestUserMessage());
        WorkingMemory memory = input.workingMemory();
        List<ConversationMessage> window = input.conversationWindow().messages();
        boolean firstTurn = window.isEmpty()
                || window.stream().noneMatch(message -> message.sender() == Sender.BANDAR);

        PlannedRule rule;
        if (firstTurn && matchesGreeting(normalized)) {
            rule = greetingRule();
        } else if (matchesGoodbye(normalized)) {
            rule = goodbyeRule();
        } else if (matchesLongHistorical(normalized)) {
            rule = longHistoricalRule();
        } else if (matchesFestival(normalized)) {
            rule = festivalRule();
        } else if (matchesStoryRequest(normalized, memory)) {
            rule = storyRule(normalized);
        } else if (memory != null && isStoryInProgress(memory)) {
            rule = continueStoryRule();
        } else if (matchesLocation(normalized)) {
            rule = locationRule();
        } else if (matchesIdentity(normalized)) {
            rule = identityRule();
        } else if (matchesCheerUp(normalized)) {
            rule = cheerUpRule();
        } else if (matchesRemember(normalized)) {
            rule = rememberRule();
        } else if (matchesReminder(normalized)) {
            rule = reminderRule();
        } else if (normalized.contains("?")) {
            rule = questionRule(normalized);
        } else if (firstTurn) {
            rule = greetingRule();
        } else {
            rule = smallTalkRule();
        }

        ConversationPlan plan = toPlan(sessionId, input.latestUserMessage(), rule);
        ConversationPlanningTrace trace = new ConversationPlanningTrace(List.of(
                new ConversationPlanningTraceEntry(rule.ruleName(), rule.reason())));
        return ConversationPlanSnapshot.planned(sessionId, plan, trace);
    }

    private ConversationPlan toPlan(String sessionId, String userMessage, PlannedRule rule) {
        int expectedMessageCount =
                ConversationMessageCountDeriver.derive(rule.energy(), sessionId, userMessage, properties);
        List<Long> delays = ConversationDelayDeriver.derive(expectedMessageCount, sessionId, userMessage, properties);
        boolean askFollowUp = resolveFollowUp(sessionId, userMessage, rule.followUpPolicy());
        return new ConversationPlan(
                rule.goal(),
                rule.confidence(),
                rule.continueConversation(),
                rule.energy(),
                rule.arc(),
                expectedMessageCount,
                delays,
                askFollowUp,
                rule.tellStory(),
                rule.tellJoke(),
                rule.tellMemory(),
                rule.endConversation(),
                rule.suggestedTone(),
                rule.outcome(),
                Instant.now(),
                false,
                false,
                null,
                null);
    }

    private static boolean resolveFollowUp(String sessionId, String userMessage, FollowUpPolicy policy) {
        int hash = ConversationDeterministicHash.hash(sessionId, userMessage, "follow-up");
        return switch (policy) {
            case NEVER -> false;
            case RARELY -> hash % 4 == 0;
            case OCCASIONALLY -> hash % 2 == 0;
            case OFTEN -> hash % 3 != 0;
        };
    }

    private static PlannedRule greetingRule() {
        return new PlannedRule(
                "greeting",
                "First turn or explicit greeting detected",
                ConversationGoal.GREETING,
                0.92,
                true,
                ConversationEnergy.LOW,
                ConversationArc.GREETING_REPLY,
                FollowUpPolicy.OCCASIONALLY,
                false,
                false,
                false,
                false,
                "Warm",
                ConversationOutcome.FOLLOW_UP_REQUIRED);
    }

    private static PlannedRule goodbyeRule() {
        return new PlannedRule(
                "goodbye",
                "User expressed thanks or farewell",
                ConversationGoal.GOODBYE,
                0.95,
                false,
                ConversationEnergy.LOW,
                ConversationArc.GOODBYE,
                FollowUpPolicy.NEVER,
                false,
                false,
                false,
                true,
                "Warm",
                ConversationOutcome.RESOLVED);
    }

    private static PlannedRule storyRule(String normalized) {
        ConversationEnergy energy = normalized.contains("long") || normalized.contains(" epic")
                ? ConversationEnergy.VERY_HIGH
                : ConversationEnergy.HIGH;
        return new PlannedRule(
                "story-request",
                "User requested a story",
                ConversationGoal.STORY,
                0.94,
                true,
                energy,
                ConversationArc.QUESTION_STORY,
                FollowUpPolicy.OFTEN,
                true,
                false,
                false,
                false,
                "Narrative",
                ConversationOutcome.STORY_STARTED);
    }

    private static PlannedRule continueStoryRule() {
        return new PlannedRule(
                "continue-story",
                "Working memory indicates a story is in progress",
                ConversationGoal.CONTINUE_STORY,
                0.9,
                true,
                ConversationEnergy.HIGH,
                ConversationArc.STORY_CONTINUATION,
                FollowUpPolicy.OFTEN,
                true,
                false,
                false,
                false,
                "Narrative",
                ConversationOutcome.STORY_COMPLETED);
    }

    private static PlannedRule locationRule() {
        return new PlannedRule(
                "location-help",
                "User asked about location (\"where\")",
                ConversationGoal.LOCATION_HELP,
                0.93,
                true,
                ConversationEnergy.LOW,
                ConversationArc.QUESTION_ANSWER,
                FollowUpPolicy.OCCASIONALLY,
                false,
                false,
                false,
                false,
                "Helpful",
                ConversationOutcome.RESOLVED);
    }

    private static PlannedRule identityRule() {
        return new PlannedRule(
                "identity",
                "User asked about their identity",
                ConversationGoal.IDENTITY,
                0.94,
                true,
                ConversationEnergy.LOW,
                ConversationArc.QUESTION_ANSWER,
                FollowUpPolicy.RARELY,
                false,
                false,
                false,
                false,
                "Reassuring",
                ConversationOutcome.RESOLVED);
    }

    private static PlannedRule cheerUpRule() {
        return new PlannedRule(
                "cheer-up",
                "User expressed boredom or low mood",
                ConversationGoal.CHEER_UP,
                0.91,
                true,
                ConversationEnergy.HIGH,
                ConversationArc.CHEER_UP,
                FollowUpPolicy.OFTEN,
                true,
                true,
                false,
                false,
                "Playful",
                ConversationOutcome.FOLLOW_UP_REQUIRED);
    }

    private static PlannedRule rememberRule() {
        return new PlannedRule(
                "remember",
                "User asked Bandar to remember something",
                ConversationGoal.REMEMBER,
                0.9,
                true,
                ConversationEnergy.MEDIUM,
                ConversationArc.REMINDER,
                FollowUpPolicy.RARELY,
                false,
                false,
                true,
                false,
                "Thoughtful",
                ConversationOutcome.PROMISE_MADE);
    }

    private static PlannedRule reminderRule() {
        return new PlannedRule(
                "reminder",
                "User asked for a reminder",
                ConversationGoal.REMINDER,
                0.88,
                true,
                ConversationEnergy.LOW,
                ConversationArc.REMINDER,
                FollowUpPolicy.OCCASIONALLY,
                false,
                false,
                false,
                false,
                "Attentive",
                ConversationOutcome.PROMISE_MADE);
    }

    private static PlannedRule questionRule(String normalized) {
        ConversationEnergy energy = normalized.length() > 80 ? ConversationEnergy.MEDIUM : ConversationEnergy.LOW;
        return new PlannedRule(
                "question",
                "User message contains a question",
                ConversationGoal.QUESTION,
                0.85,
                true,
                energy,
                ConversationArc.QUESTION_ANSWER,
                FollowUpPolicy.OCCASIONALLY,
                false,
                false,
                false,
                false,
                "Curious",
                ConversationOutcome.QUESTION_LEFT_OPEN);
    }

    private static PlannedRule smallTalkRule() {
        return new PlannedRule(
                "small-talk",
                "No stronger rule matched; default conversational reply",
                ConversationGoal.SMALL_TALK,
                0.65,
                true,
                ConversationEnergy.LOW,
                ConversationArc.SMALL_TALK,
                FollowUpPolicy.NEVER,
                false,
                false,
                false,
                false,
                "Thoughtful",
                ConversationOutcome.UNRESOLVED);
    }

    private static PlannedRule festivalRule() {
        return new PlannedRule(
                "festival",
                "User mentioned a festival or celebration",
                ConversationGoal.STORY,
                0.93,
                true,
                ConversationEnergy.HIGH,
                ConversationArc.QUESTION_STORY,
                FollowUpPolicy.OFTEN,
                true,
                false,
                false,
                false,
                "Festive",
                ConversationOutcome.STORY_STARTED);
    }

    private static PlannedRule longHistoricalRule() {
        return new PlannedRule(
                "long-historical",
                "User requested a long historical discussion",
                ConversationGoal.QUESTION,
                0.92,
                true,
                ConversationEnergy.VERY_HIGH,
                ConversationArc.QUESTION_ANSWER,
                FollowUpPolicy.OCCASIONALLY,
                false,
                false,
                false,
                false,
                "Reflective",
                ConversationOutcome.QUESTION_LEFT_OPEN);
    }

    private static boolean matchesGreeting(String normalized) {
        return normalized.equals("hello")
                || normalized.equals("hi")
                || normalized.equals("hey")
                || normalized.startsWith("hello ")
                || normalized.startsWith("hi ")
                || normalized.startsWith("good morning")
                || normalized.startsWith("good evening")
                || normalized.startsWith("namaste");
    }

    private static boolean matchesGoodbye(String normalized) {
        return normalized.contains("thank you")
                || normalized.contains("thanks")
                || normalized.equals("bye")
                || normalized.startsWith("bye ")
                || normalized.contains("goodbye")
                || normalized.contains("see you");
    }

    private static boolean matchesStoryRequest(String normalized, WorkingMemory memory) {
        if (normalized.contains("tell me a story")
                || normalized.contains("tell a story")
                || normalized.contains("story please")) {
            return true;
        }
        return normalized.contains("story") && normalized.contains("tell");
    }

    private static boolean isStoryInProgress(WorkingMemory memory) {
        String story = memory.currentStory() == null ? "" : memory.currentStory().toLowerCase(Locale.ROOT);
        return story.contains("in progress");
    }

    private static boolean matchesLocation(String normalized) {
        return normalized.contains("where am i")
                || normalized.contains("where are we")
                || normalized.contains("where am i?")
                || (normalized.contains("where") && normalized.contains("?"));
    }

    private static boolean matchesIdentity(String normalized) {
        return normalized.contains("who am i")
                || normalized.contains("do you know me")
                || normalized.contains("who are you talking to");
    }

    private static boolean matchesCheerUp(String normalized) {
        return normalized.contains("i'm bored")
                || normalized.contains("im bored")
                || normalized.contains("i am bored")
                || normalized.contains("cheer me up")
                || normalized.contains("feeling sad")
                || normalized.contains("i am sad");
    }

    private static boolean matchesRemember(String normalized) {
        return normalized.contains("remember this")
                || normalized.contains("remember that")
                || normalized.contains("don't forget")
                || normalized.contains("do not forget");
    }

    private static boolean matchesReminder(String normalized) {
        return normalized.contains("remind me");
    }

    private static boolean matchesFestival(String normalized) {
        return normalized.contains("festival") || normalized.contains("celebration");
    }

    private static boolean matchesLongHistorical(String normalized) {
        return (normalized.contains("history") || normalized.contains("historical"))
                && (normalized.length() > 60 || normalized.contains("long"));
    }

    private static String normalize(String message) {
        return message == null ? "" : message.toLowerCase(Locale.ROOT).trim();
    }

    private enum FollowUpPolicy {
        NEVER,
        RARELY,
        OCCASIONALLY,
        OFTEN
    }

    private record PlannedRule(
            String ruleName,
            String reason,
            ConversationGoal goal,
            double confidence,
            boolean continueConversation,
            ConversationEnergy energy,
            ConversationArc arc,
            FollowUpPolicy followUpPolicy,
            boolean tellStory,
            boolean tellJoke,
            boolean tellMemory,
            boolean endConversation,
            String suggestedTone,
            ConversationOutcome outcome) {}
}
