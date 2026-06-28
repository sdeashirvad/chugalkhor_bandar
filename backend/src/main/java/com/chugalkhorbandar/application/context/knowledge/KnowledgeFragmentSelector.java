package com.chugalkhorbandar.application.context.knowledge;



import java.util.LinkedHashMap;

import java.util.Locale;

import java.util.Map;

import java.util.Set;

import org.springframework.stereotype.Component;



@Component

public class KnowledgeFragmentSelector {



    public Map<KnowledgeFragmentType, String> select(String latestUserMessage) {

        String normalized = normalize(latestUserMessage);

        Map<KnowledgeFragmentType, String> selections = new LinkedHashMap<>();



        selections.put(KnowledgeFragmentType.IDENTITY, "Always included");

        selections.put(KnowledgeFragmentType.SPEAKING_STYLE, "Always included");

        selections.put(KnowledgeFragmentType.CONVERSATION, "Always included");

        selections.put(KnowledgeFragmentType.WORKING_MEMORY, "Session-scoped working memory");



        if (normalized.isBlank()) {

            return selections;

        }



        if (isIdentityQuery(normalized)) {

            selections.put(KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR, "User asked about their identity");

            selections.put(KnowledgeFragmentType.CHARACTER_PROFILE, "User asked about their identity");

            selections.put(KnowledgeFragmentType.CHARACTER_TITLES, "User asked about their identity");

            selections.put(KnowledgeFragmentType.CHARACTER_LOCATION, "User asked about their identity");

            return selections;

        }



        if (normalized.contains("where")) {

            selections.put(KnowledgeFragmentType.CHARACTER_LOCATION, "User asked about location (\"where\")");

            selections.put(KnowledgeFragmentType.CHARACTER_PROFILE, "User asked about location (\"where\")");

            return selections;

        }



        if (normalized.contains("who")) {

            selections.put(KnowledgeFragmentType.CHARACTER_PROFILE, "User asked about identity (\"who\")");

            selections.put(KnowledgeFragmentType.CHARACTER_TITLES, "User asked about identity (\"who\")");

            return selections;

        }



        boolean addedSpecific = false;



        if (normalized.contains("story")) {

            selections.put(KnowledgeFragmentType.STORYTELLING, "User asked about stories (\"story\")");

            selections.put(KnowledgeFragmentType.STORY_SUMMARY, "User asked about stories (\"story\")");

            selections.put(KnowledgeFragmentType.WORLD_HISTORY, "User asked about stories (\"story\")");

            addedSpecific = true;

        }



        if (normalized.contains("remember")) {

            selections.put(KnowledgeFragmentType.WORLD_HISTORY, "User asked about memory (\"remember\")");

            addedSpecific = true;

        }



        if (normalized.contains("relationship") || normalized.contains("king")) {

            selections.put(KnowledgeFragmentType.CHARACTER_RELATIONSHIPS, "Relationship signal in message");

            selections.put(KnowledgeFragmentType.CHARACTER_PROFILE, "Relationship signal in message");

            selections.put(KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR, "Relationship signal in message");

            addedSpecific = true;

        }



        if (normalized.contains("history")) {

            selections.put(KnowledgeFragmentType.WORLD_HISTORY, "User asked about history");

            addedSpecific = true;

        }



        if (normalized.contains("politic")) {

            selections.put(KnowledgeFragmentType.WORLD_POLITICS, "User asked about politics");

            addedSpecific = true;

        }



        if (normalized.contains("econom") || normalized.contains("trade") || normalized.contains("coin")) {

            selections.put(KnowledgeFragmentType.WORLD_ECONOMY, "User asked about economy");

            addedSpecific = true;

        }



        if (normalized.contains("transport") || normalized.contains("travel") || normalized.contains("horse")) {

            selections.put(KnowledgeFragmentType.WORLD_TRANSPORT, "User asked about transport");

            addedSpecific = true;

        }



        if (normalized.contains("species") || normalized.contains("hippu") || normalized.contains("rabbitu")) {

            selections.put(KnowledgeFragmentType.WORLD_SPECIES, "User asked about species");

            addedSpecific = true;

        }



        if (normalized.contains("world") || normalized.contains("jungle") || normalized.contains("canon")) {

            selections.put(KnowledgeFragmentType.WORLD_GEOGRAPHY, "User asked about the world");

            addedSpecific = true;

        }



        if (!addedSpecific) {

            selections.put(KnowledgeFragmentType.STORYTELLING, "Balanced general chat");

            selections.put(KnowledgeFragmentType.HUMOR, "Balanced general chat");

            selections.put(KnowledgeFragmentType.WORLD_GEOGRAPHY, "Balanced general chat");

        }



        return selections;

    }



    public KnowledgeFragmentPlanningTrace trace(Map<KnowledgeFragmentType, String> selections) {

        return new KnowledgeFragmentPlanningTrace(selections.entrySet().stream()

                .map(entry -> new KnowledgeFragmentSelectionEntry(entry.getKey(), entry.getValue()))

                .toList());

    }



    public Set<KnowledgeFragmentType> alwaysIncluded() {

        return Set.of(

                KnowledgeFragmentType.IDENTITY,

                KnowledgeFragmentType.SPEAKING_STYLE,

                KnowledgeFragmentType.CONVERSATION);

    }



    private static boolean isIdentityQuery(String normalized) {

        return normalized.contains("who am i")

                || normalized.contains("do you know me")

                || normalized.contains("who are you talking to");

    }



    private static String normalize(String message) {

        if (message == null) {

            return "";

        }

        return message.toLowerCase(Locale.ROOT).trim();

    }

}


