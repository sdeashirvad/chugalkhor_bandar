package com.chugalkhorbandar.application.memory.working;

import com.chugalkhorbandar.application.context.RuntimeWorldContext;
import com.chugalkhorbandar.application.session.ChatSession;
import com.chugalkhorbandar.application.session.CurrentCharacter;
import com.chugalkhorbandar.domain.conversation.Conversation;
import com.chugalkhorbandar.domain.conversation.ConversationMessage;
import com.chugalkhorbandar.domain.conversation.Sender;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class WorkingMemoryBuilder {

    private static final Pattern QUESTION_PATTERN = Pattern.compile("\\?");
    private static final Pattern PROMISE_PATTERN = Pattern.compile(
            "\\b(i will|i'll|let me|i promise|next i shall)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern FACT_PATTERN = Pattern.compile(
            "\\b(you are|we are at|remember that|you're in)\\b", Pattern.CASE_INSENSITIVE);
    private static final List<String> CURATED_ENTITIES = List.of(
            "Hippu King",
            "Rabbitu Minister",
            "Giraffe Sir",
            "Bandar",
            "Home Jungle",
            "Hippu Palace");

    private final WorkingMemoryProperties properties;

    public WorkingMemoryBuilder(WorkingMemoryProperties properties) {
        this.properties = properties;
    }

    public WorkingMemorySnapshot build(
            CurrentCharacter user,
            ChatSession session,
            Conversation conversation,
            RuntimeWorldContext runtimeWorld,
            long previousVersion) {
        List<ConversationMessage> messages = conversation == null ? List.of() : conversation.messages();
        List<ConversationMessage> analysisWindow = tail(messages, properties.getAnalysisWindowMessages());
        String combined = normalize(combine(analysisWindow));
        String latestUser = latestUserMessage(analysisWindow);

        FieldResult<String> activeTopic = detectActiveTopic(combined, latestUser, analysisWindow, runtimeWorld);
        FieldResult<String> mood = detectMood(analysisWindow, combined);
        FieldResult<String> currentStory = detectCurrentStory(analysisWindow, combined);
        FieldResult<List<String>> entities = detectActiveEntities(analysisWindow, runtimeWorld);
        FieldResult<List<String>> unanswered = detectUnansweredQuestions(analysisWindow);
        FieldResult<List<String>> promises = detectRecentPromises(analysisWindow);
        FieldResult<List<String>> facts = detectImportantFacts(analysisWindow, user, activeTopic.value());

        if (facts.value().isEmpty() && user.currentLocation() != null && !user.currentLocation().isBlank()) {
            facts = new FieldResult<>(
                    List.of("The speaker's current location is " + user.currentLocation() + "."),
                    "Speaker current location from session");
        }

        WorkingMemory memory = new WorkingMemory(
                session.sessionId(),
                activeTopic.value(),
                mood.value(),
                currentStory.value(),
                entities.value(),
                unanswered.value(),
                promises.value(),
                facts.value(),
                Instant.now(),
                previousVersion + 1);

        List<WorkingMemoryFieldTrace> traces = List.of(
                traceString("activeTopic", activeTopic),
                traceString("conversationMood", mood),
                traceString("currentStory", currentStory),
                traceList("activeEntities", entities),
                traceList("unansweredQuestions", unanswered),
                traceList("recentPromises", promises),
                traceList("importantFacts", facts));

        return new WorkingMemorySnapshot(memory, traces);
    }

    private static FieldResult<String> detectActiveTopic(
            String combined,
            String latestUser,
            List<ConversationMessage> window,
            RuntimeWorldContext runtimeWorld) {
        String normalizedLatest = normalize(latestUser);

        if (normalizedLatest.contains("who am i")
                || normalizedLatest.contains("do you know me")
                || normalizedLatest.contains("who are you talking to")) {
            return new FieldResult<>("Identity", "Identity query in latest user message");
        }
        if (normalizedLatest.contains("where") || combined.contains("where am i") || combined.contains("where are we")) {
            return new FieldResult<>("Location", "Location signal (\"where\") in recent messages");
        }
        if (combined.contains("founding") || combined.contains("origin story")) {
            return new FieldResult<>("Founding Story", "Founding or origin story mentioned");
        }
        if (combined.contains("hippu king") || (combined.contains("hippu") && combined.contains("king"))) {
            return new FieldResult<>("Hippu King", "Repeated Hippu King mentions in recent window");
        }
        if (combined.contains("rabbitu minister") || combined.contains("rabbitu")) {
            return new FieldResult<>("Rabbitu Minister", "Rabbitu mentioned in recent window");
        }
        if (combined.contains("palace")) {
            return new FieldResult<>("Palace", "Palace mentioned in recent window");
        }
        if (combined.contains("story") || normalizedLatest.contains("tell me")) {
            return new FieldResult<>("Storytelling", "Story request or storytelling signal");
        }

        String topEntity = topMentionedEntity(window, runtimeWorld);
        if (topEntity != null) {
            return new FieldResult<>(topEntity, "Most repeated entity in recent message window");
        }

        if (window.isEmpty()) {
            return new FieldResult<>("General Conversation", "No messages yet; default topic");
        }
        return new FieldResult<>("General Conversation", "No dominant topic signal in recent window");
    }

    private static FieldResult<String> detectMood(List<ConversationMessage> window, String combined) {
        long userQuestions = window.stream()
                .filter(message -> message.sender() == Sender.USER)
                .filter(message -> QUESTION_PATTERN.matcher(message.content()).find())
                .count();
        if (userQuestions >= 3) {
            return new FieldResult<>("Curious", "Three or more user questions in analysis window");
        }
        if (combined.contains("haha")
                || combined.contains("joke")
                || combined.contains("funny")
                || combined.contains("playful")
                || combined.contains("hehe")) {
            return new FieldResult<>("Playful", "Playful language in recent messages");
        }
        if (combined.contains("remember")
                || combined.contains("long ago")
                || combined.contains("those days")
                || combined.contains("nostalg")) {
            return new FieldResult<>("Nostalgic", "Nostalgia signal in recent messages");
        }
        if (combined.contains("war")
                || combined.contains("law")
                || combined.contains("danger")
                || combined.contains("death")
                || combined.contains("serious")) {
            return new FieldResult<>("Serious", "Serious subject matter in recent messages");
        }
        if (userQuestions >= 1) {
            return new FieldResult<>("Curious", "User question present in analysis window");
        }
        return new FieldResult<>("Thoughtful", "Default conversational tone");
    }

    private static FieldResult<String> detectCurrentStory(List<ConversationMessage> window, String combined) {
        boolean storyRequested = combined.contains("tell me a story")
                || combined.contains("tell a story")
                || combined.contains("story please")
                || (combined.contains("story") && combined.contains("tell"));
        if (!storyRequested && !combined.contains("once upon")) {
            return new FieldResult<>("", "No active story signal");
        }

        ConversationMessage lastUserStoryRequest = null;
        for (int index = window.size() - 1; index >= 0; index--) {
            ConversationMessage message = window.get(index);
            if (message.sender() == Sender.USER && normalize(message.content()).contains("story")) {
                lastUserStoryRequest = message;
                break;
            }
        }

        if (lastUserStoryRequest == null) {
            if (combined.contains("once upon")) {
                return new FieldResult<>("Story in progress", "Narrative opening detected in recent messages");
            }
            return new FieldResult<>("", "No active story signal");
        }

        int requestIndex = window.indexOf(lastUserStoryRequest);
        boolean bandarRepliedWithSubstance = false;
        for (int index = requestIndex + 1; index < window.size(); index++) {
            ConversationMessage message = window.get(index);
            if (message.sender() == Sender.BANDAR && message.content().trim().length() >= 80) {
                bandarRepliedWithSubstance = true;
                break;
            }
        }

        if (!bandarRepliedWithSubstance) {
            return new FieldResult<>("Story requested, not yet started", "User requested a story without a substantive Bandar reply");
        }
        return new FieldResult<>("Story in progress", "User requested a story and Bandar began narrating");
    }

    private FieldResult<List<String>> detectActiveEntities(
            List<ConversationMessage> window, RuntimeWorldContext runtimeWorld) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        List<String> candidates = new ArrayList<>(CURATED_ENTITIES);
        if (runtimeWorld != null && runtimeWorld.knownEntityLabels() != null) {
            candidates.addAll(runtimeWorld.knownEntityLabels());
        }

        String combined = combine(window);
        for (String entity : candidates) {
            if (entity == null || entity.isBlank()) {
                continue;
            }
            int occurrences = countOccurrences(combined, entity);
            if (occurrences > 0) {
                counts.put(entity, occurrences);
            }
        }

        List<String> ranked = counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        if (ranked.isEmpty()) {
            return new FieldResult<>(List.of(), "No repeated entities in recent window");
        }
        return new FieldResult<>(ranked, "Entities ranked by mention count in analysis window");
    }

    private static FieldResult<List<String>> detectUnansweredQuestions(List<ConversationMessage> window) {
        LinkedHashSet<String> unanswered = new LinkedHashSet<>();
        for (int index = 0; index < window.size(); index++) {
            ConversationMessage message = window.get(index);
            if (message.sender() != Sender.USER || !QUESTION_PATTERN.matcher(message.content()).find()) {
                continue;
            }
            String question = message.content().trim();
            boolean answered = false;
            for (int replyIndex = index + 1; replyIndex < window.size(); replyIndex++) {
                ConversationMessage reply = window.get(replyIndex);
                if (reply.sender() == Sender.USER) {
                    break;
                }
                if (reply.sender() == Sender.BANDAR && reply.content().trim().length() >= 30) {
                    answered = true;
                    break;
                }
            }
            if (!answered) {
                unanswered.add(question);
            }
        }
        List<String> results = unanswered.stream().limit(5).toList();
        if (results.isEmpty()) {
            return new FieldResult<>(List.of(), "No unanswered user questions in analysis window");
        }
        return new FieldResult<>(results, "User questions without a substantive Bandar reply");
    }

    private static FieldResult<List<String>> detectRecentPromises(List<ConversationMessage> window) {
        List<String> promises = window.stream()
                .filter(message -> message.sender() == Sender.BANDAR)
                .map(ConversationMessage::content)
                .filter(content -> PROMISE_PATTERN.matcher(content).find())
                .map(String::trim)
                .distinct()
                .limit(3)
                .toList();
        if (promises.isEmpty()) {
            return new FieldResult<>(List.of(), "No promise phrases in recent Bandar messages");
        }
        return new FieldResult<>(promises, "Bandar messages containing promise phrases");
    }

    private static FieldResult<List<String>> detectImportantFacts(
            List<ConversationMessage> window, CurrentCharacter user, String activeTopic) {
        List<String> facts = new ArrayList<>();
        window.stream()
                .filter(message -> message.sender() == Sender.BANDAR)
                .map(ConversationMessage::content)
                .filter(content -> FACT_PATTERN.matcher(content).find())
                .map(String::trim)
                .distinct()
                .limit(3)
                .forEach(facts::add);

        if ("Location".equals(activeTopic) && user.currentLocation() != null && !user.currentLocation().isBlank()) {
            facts.add("The speaker may be asking about " + user.currentLocation() + ".");
        }

        List<String> limited = facts.stream().distinct().limit(5).toList();
        if (limited.isEmpty()) {
            return new FieldResult<>(List.of(), "No temporary observation phrases in recent Bandar messages");
        }
        return new FieldResult<>(limited, "Bandar observations and location context relevant to this conversation");
    }

    private static String topMentionedEntity(List<ConversationMessage> window, RuntimeWorldContext runtimeWorld) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        List<String> candidates = new ArrayList<>(CURATED_ENTITIES);
        if (runtimeWorld != null && runtimeWorld.knownEntityLabels() != null) {
            candidates.addAll(runtimeWorld.knownEntityLabels());
        }
        String combined = combine(window);
        for (String entity : candidates) {
            if (entity == null || entity.isBlank()) {
                continue;
            }
            int occurrences = countOccurrences(combined, entity);
            if (occurrences > 0) {
                counts.put(entity, occurrences);
            }
        }
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static List<ConversationMessage> tail(List<ConversationMessage> messages, int maxMessages) {
        if (messages.isEmpty()) {
            return List.of();
        }
        int limit = Math.max(1, maxMessages);
        if (messages.size() <= limit) {
            return List.copyOf(messages);
        }
        return List.copyOf(messages.subList(messages.size() - limit, messages.size()));
    }

    private static String latestUserMessage(List<ConversationMessage> window) {
        for (int index = window.size() - 1; index >= 0; index--) {
            ConversationMessage message = window.get(index);
            if (message.sender() == Sender.USER) {
                return message.content();
            }
        }
        return "";
    }

    private static String combine(List<ConversationMessage> window) {
        return window.stream().map(ConversationMessage::content).reduce("", (left, right) -> left + " " + right);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    private static int countOccurrences(String haystack, String needle) {
        String normalizedHaystack = normalize(haystack);
        String normalizedNeedle = normalize(needle);
        if (normalizedNeedle.isBlank()) {
            return 0;
        }
        int count = 0;
        int fromIndex = 0;
        while (fromIndex >= 0) {
            fromIndex = normalizedHaystack.indexOf(normalizedNeedle, fromIndex);
            if (fromIndex >= 0) {
                count++;
                fromIndex += normalizedNeedle.length();
            }
        }
        return count;
    }

    private static WorkingMemoryFieldTrace traceString(String field, FieldResult<String> result) {
        return new WorkingMemoryFieldTrace(field, result.value(), result.heuristic());
    }

    private static WorkingMemoryFieldTrace traceList(String field, FieldResult<List<String>> result) {
        String value = result.value().isEmpty() ? "(none)" : String.join("; ", result.value());
        return new WorkingMemoryFieldTrace(field, value, result.heuristic());
    }

    private record FieldResult<T>(T value, String heuristic) {}
}
