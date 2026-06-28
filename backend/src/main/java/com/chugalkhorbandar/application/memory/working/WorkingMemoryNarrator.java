package com.chugalkhorbandar.application.memory.working;

import java.util.ArrayList;
import java.util.List;

public final class WorkingMemoryNarrator {

    private WorkingMemoryNarrator() {}

    public static String narrate(WorkingMemory memory) {
        if (memory == null) {
            return "No working memory yet for this session.";
        }

        List<String> lines = new ArrayList<>();
        lines.add(topicLine(memory.activeTopic(), memory.activeEntities()));
        lines.add(moodLine(memory.conversationMood()));
        lines.addAll(entityLines(memory.activeEntities(), memory.activeTopic()));
        lines.addAll(questionLines(memory.unansweredQuestions()));
        lines.add(storyLine(memory.currentStory()));
        lines.addAll(promiseLines(memory.recentPromises()));
        lines.addAll(factLines(memory.importantFacts()));

        return lines.stream().filter(line -> line != null && !line.isBlank()).reduce((left, right) -> left + "\n" + right).orElse("");
    }

    private static String topicLine(String activeTopic, List<String> entities) {
        if (activeTopic == null || activeTopic.isBlank() || "General Conversation".equals(activeTopic)) {
            return "We are having a general conversation.";
        }
        if (entities != null && !entities.isEmpty() && entities.get(0).equals(activeTopic)) {
            return "We have been discussing " + activeTopic + ".";
        }
        return "We have been discussing " + activeTopic + ".";
    }

    private static String moodLine(String mood) {
        if (mood == null || mood.isBlank()) {
            return "";
        }
        return "The conversation feels " + mood.toLowerCase() + ".";
    }

    private static List<String> entityLines(List<String> entities, String activeTopic) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        List<String> others = entities.stream().filter(entity -> !entity.equals(activeTopic)).toList();
        if (others.isEmpty()) {
            return List.of();
        }
        return List.of("Important names right now: " + String.join(", ", others) + ".");
    }

    private static List<String> questionLines(List<String> unansweredQuestions) {
        if (unansweredQuestions == null || unansweredQuestions.isEmpty()) {
            return List.of();
        }
        List<String> lines = new ArrayList<>();
        for (String question : unansweredQuestions) {
            lines.add("The user previously asked: " + question);
        }
        return lines;
    }

    private static String storyLine(String currentStory) {
        if (currentStory == null || currentStory.isBlank()) {
            return "A story has not yet been started.";
        }
        return switch (currentStory) {
            case "Story requested, not yet started" -> "The user asked for a story, but I have not properly begun one.";
            case "Story in progress" -> "A story is currently in progress.";
            default -> currentStory.endsWith(".") ? currentStory : currentStory + ".";
        };
    }

    private static List<String> promiseLines(List<String> recentPromises) {
        if (recentPromises == null || recentPromises.isEmpty()) {
            return List.of("No promises remain outstanding.");
        }
        return List.of("I recently said I would: " + String.join(" ", recentPromises));
    }

    private static List<String> factLines(List<String> importantFacts) {
        if (importantFacts == null || importantFacts.isEmpty()) {
            return List.of();
        }
        List<String> lines = new ArrayList<>();
        for (String fact : importantFacts) {
            lines.add(fact.endsWith(".") ? fact : fact + ".");
        }
        return lines;
    }
}
