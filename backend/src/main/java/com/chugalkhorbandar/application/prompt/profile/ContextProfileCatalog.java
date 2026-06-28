package com.chugalkhorbandar.application.prompt.profile;

import com.chugalkhorbandar.application.prompt.PromptSectionPriorities;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.config.PromptProfileProperties;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ContextProfileCatalog {

    private final Map<ContextProfileType, ContextProfile> profiles;

    public ContextProfileCatalog(PromptProfileProperties properties) {
        this.profiles = buildProfiles(properties);
    }

    public ContextProfile profile(ContextProfileType type) {
        return profiles.get(type);
    }

    public Map<ContextProfileType, ContextProfile> allProfiles() {
        return Map.copyOf(profiles);
    }

    private static Map<ContextProfileType, ContextProfile> buildProfiles(PromptProfileProperties properties) {
        Map<ContextProfileType, ContextProfile> catalog = new EnumMap<>(ContextProfileType.class);
        for (ContextProfileType type : ContextProfileType.values()) {
            catalog.put(type, definitionFor(type, properties));
        }
        return catalog;
    }

    private static ContextProfile definitionFor(ContextProfileType type, PromptProfileProperties properties) {
        Set<PromptSectionType> minimumRequired = coreRequiredSections();
        Map<PromptSectionType, Integer> priorities = defaultPriorities(properties, type);

        return switch (type) {
            case GENERAL_CHAT -> new ContextProfile(
                    type,
                    "General Chat",
                    "Balanced distribution for open-ended conversation.",
                    Set.of(
                            PromptSectionType.PERSONALITY,
                            PromptSectionType.CURRENT_CONVERSATION,
                            PromptSectionType.WORLD_FACTS),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(),
                    priorities);
            case LOCATION_QUERY -> new ContextProfile(
                    type,
                    "Location Query",
                    "Prioritize place and character context for location questions.",
                    Set.of(
                            PromptSectionType.CURRENT_LOCATION,
                            PromptSectionType.CURRENT_CHARACTER,
                            PromptSectionType.CURRENT_CONVERSATION),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(PromptSectionType.WORLD_FACTS, PromptSectionType.RELEVANT_STORIES),
                    priorities);
            case IDENTITY_QUERY -> new ContextProfile(
                    type,
                    "Identity Query",
                    "Prioritize who is speaking and Bandar's relationship to them.",
                    Set.of(
                            PromptSectionType.CURRENT_USER,
                            PromptSectionType.CURRENT_CHARACTER,
                            PromptSectionType.CURRENT_LOCATION,
                            PromptSectionType.RELATIONSHIP_TO_BANDAR,
                            PromptSectionType.CURRENT_CONVERSATION),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(
                            PromptSectionType.RELEVANT_STORIES,
                            PromptSectionType.WORLD_FACTS,
                            PromptSectionType.PUBLIC_EVENTS),
                    priorities);
            case CHARACTER_QUERY -> new ContextProfile(
                    type,
                    "Character Query",
                    "Prioritize character identity and relationships.",
                    Set.of(
                            PromptSectionType.CURRENT_CHARACTER,
                            PromptSectionType.RELATIONSHIPS,
                            PromptSectionType.CURRENT_CONVERSATION),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(PromptSectionType.RELEVANT_STORIES, PromptSectionType.PUBLIC_EVENTS),
                    priorities);
            case STORY_QUERY -> new ContextProfile(
                    type,
                    "Story Query",
                    "Prioritize narrative and world canon for story questions.",
                    Set.of(
                            PromptSectionType.RELEVANT_STORIES,
                            PromptSectionType.WORLD_FACTS,
                            PromptSectionType.CURRENT_CONVERSATION),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(PromptSectionType.CURRENT_LOCATION),
                    priorities);
            case WORLD_QUERY -> new ContextProfile(
                    type,
                    "World Query",
                    "Prioritize world facts and public events.",
                    Set.of(
                            PromptSectionType.WORLD_FACTS,
                            PromptSectionType.PUBLIC_EVENTS,
                            PromptSectionType.CURRENT_CONVERSATION),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(PromptSectionType.CURRENT_LOCATION, PromptSectionType.RELATIONSHIPS),
                    priorities);
            case MEMORY_QUERY -> new ContextProfile(
                    type,
                    "Memory Query",
                    "Prioritize retrieved and session memory.",
                    Set.of(
                            PromptSectionType.LONG_TERM_MEMORY,
                            PromptSectionType.SESSION_SUMMARY,
                            PromptSectionType.CURRENT_CONVERSATION),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(PromptSectionType.RELEVANT_STORIES, PromptSectionType.PUBLIC_EVENTS),
                    priorities);
            case RELATIONSHIP_QUERY -> new ContextProfile(
                    type,
                    "Relationship Query",
                    "Prioritize relationships and character context.",
                    Set.of(
                            PromptSectionType.RELATIONSHIPS,
                            PromptSectionType.CURRENT_CHARACTER,
                            PromptSectionType.CURRENT_CONVERSATION),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(PromptSectionType.RELEVANT_STORIES, PromptSectionType.LONG_TERM_MEMORY),
                    priorities);
            case UNKNOWN -> new ContextProfile(
                    type,
                    "Unknown",
                    "Minimal profile when intent cannot be determined.",
                    Set.of(PromptSectionType.CURRENT_CONVERSATION),
                    optionalContextSections(),
                    minimumRequired,
                    Set.of(
                            PromptSectionType.CURRENT_LOCATION,
                            PromptSectionType.RELEVANT_STORIES,
                            PromptSectionType.RELATIONSHIPS,
                            PromptSectionType.PUBLIC_EVENTS,
                            PromptSectionType.LONG_TERM_MEMORY,
                            PromptSectionType.SESSION_SUMMARY,
                            PromptSectionType.SECRET_MEMORY),
                    priorities);
        };
    }

    private static Set<PromptSectionType> coreRequiredSections() {
        return Set.of(
                PromptSectionType.CURRENT_USER,
                PromptSectionType.PERSONALITY,
                PromptSectionType.WORLD_FACTS,
                PromptSectionType.CURRENT_CONVERSATION,
                PromptSectionType.USER_MESSAGE,
                PromptSectionType.INSTRUCTIONS);
    }

    private static Set<PromptSectionType> optionalContextSections() {
        Set<PromptSectionType> optional = new LinkedHashSet<>();
        for (PromptSectionType type : PromptSectionType.values()) {
            if (!coreRequiredSections().contains(type)
                    && type != PromptSectionType.USER_MESSAGE
                    && type != PromptSectionType.INSTRUCTIONS) {
                optional.add(type);
            }
        }
        return Set.copyOf(optional);
    }

    private static Map<PromptSectionType, Integer> defaultPriorities(
            PromptProfileProperties properties, ContextProfileType profileType) {
        Map<PromptSectionType, Integer> priorities = new EnumMap<>(PromptSectionType.class);
        for (PromptSectionType type : PromptSectionType.values()) {
            priorities.put(type, PromptSectionPriorities.priority(type));
        }
        Map<String, Integer> overrides = properties.getProfiles().get(profileType.name().toLowerCase().replace('_', '-'));
        if (overrides != null) {
            overrides.forEach((key, value) -> {
                try {
                    priorities.put(PromptSectionType.valueOf(key.toUpperCase()), value);
                } catch (IllegalArgumentException ignored) {
                    // Ignore unknown section keys in configuration.
                }
            });
        }
        return priorities;
    }
}
