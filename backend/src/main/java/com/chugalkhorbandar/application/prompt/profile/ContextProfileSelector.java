package com.chugalkhorbandar.application.prompt.profile;



import com.chugalkhorbandar.application.context.ContextPlan;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;

import com.chugalkhorbandar.application.context.ContextPlanningTraceEntry;

import com.chugalkhorbandar.application.context.ContextSectionType;

import com.chugalkhorbandar.application.context.resolver.ResolvedContext;

import java.util.Locale;

import org.springframework.stereotype.Component;



@Component

public class ContextProfileSelector {



    private final ContextProfileCatalog catalog;



    public ContextProfileSelector(ContextProfileCatalog catalog) {

        this.catalog = catalog;

    }



    public ProfileSelection select(

            ContextPlannerRequest request, ContextPlan plan, ResolvedContext resolvedContext) {

        String normalized = normalize(request.latestUserMessage());

        if (normalized.isBlank()) {

            return new ProfileSelection(

                    catalog.profile(ContextProfileType.UNKNOWN), "Empty or missing user message");

        }



        if (isIdentityQuery(normalized)) {

            return selected(ContextProfileType.IDENTITY_QUERY, "User message is an identity question");

        }

        if (normalized.contains("where")) {

            return selected(ContextProfileType.LOCATION_QUERY, "User message contains \"where\"");

        }

        if (normalized.contains("who")) {

            return selected(ContextProfileType.CHARACTER_QUERY, "User message contains \"who\"");

        }

        if (normalized.contains("story")) {

            return selected(ContextProfileType.STORY_QUERY, "User message contains \"story\"");

        }

        if (normalized.contains("remember")) {

            return selected(ContextProfileType.MEMORY_QUERY, "User message contains \"remember\"");

        }

        if (containsRelationshipSignal(normalized, plan)) {

            return selected(ContextProfileType.RELATIONSHIP_QUERY, "Relationship signal in message or planner trace");

        }

        if (containsWorldSignal(normalized)) {

            return selected(ContextProfileType.WORLD_QUERY, "User message contains world-related keywords");

        }

        if (resolvedContext.totalEstimatedTokens() == 0) {

            return selected(ContextProfileType.UNKNOWN, "Resolved context is empty");

        }



        return selected(ContextProfileType.GENERAL_CHAT, "No specific intent keyword matched");

    }



    private ProfileSelection selected(ContextProfileType type, String reason) {

        return new ProfileSelection(catalog.profile(type), reason);

    }



    private static boolean isIdentityQuery(String normalized) {

        return normalized.contains("who am i")

                || normalized.contains("do you know me")

                || normalized.contains("who are you talking to");

    }



    private static boolean containsRelationshipSignal(String normalized, ContextPlan plan) {

        if (normalized.contains("relationship")

                || normalized.contains("related to")

                || normalized.contains("king")) {

            return true;

        }

        return plan.trace().entries().stream()

                .map(ContextPlanningTraceEntry::type)

                .anyMatch(type -> type == ContextSectionType.RELATIONSHIPS);

    }



    private static boolean containsWorldSignal(String normalized) {

        return normalized.contains("world")

                || normalized.contains("jungle")

                || normalized.contains("canon")

                || normalized.contains("territory");

    }



    private static String normalize(String message) {

        if (message == null) {

            return "";

        }

        return message.toLowerCase(Locale.ROOT).trim();

    }

}


