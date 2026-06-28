package com.chugalkhorbandar.application.context.knowledge;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionPriorities;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class KnowledgeFragmentLegacyAdapter {

    private KnowledgeFragmentLegacyAdapter() {}

    public static List<ContextSection> toContextSections(KnowledgeFragmentPlan fragmentPlan) {
        Map<ContextSectionType, ContextSection> sections = new LinkedHashMap<>();
        for (KnowledgeFragmentRequest request : fragmentPlan.requests()) {
            ContextSectionType sectionType = toContextSectionType(request.fragmentType());
            sections.putIfAbsent(
                    sectionType,
                    new ContextSection(
                            sectionType,
                            ContextSectionPriorities.priority(sectionType),
                            request.reference().provider(),
                            request.reference(),
                            ContextSection.estimateTokens(request.reference())));
        }
        return List.copyOf(sections.values());
    }

    public static List<ResolvedContextSection> toResolvedSections(
            List<KnowledgeFragment> fragments, KnowledgeFragmentPlan fragmentPlan) {
        Map<ContextSectionType, StringBuilder> grouped = new LinkedHashMap<>();
        Map<ContextSectionType, ContextReference> references = new LinkedHashMap<>();
        Map<ContextSectionType, String> sources = new LinkedHashMap<>();

        for (KnowledgeFragment fragment : fragments) {
            ContextSectionType sectionType = toContextSectionType(fragment.fragmentType());
            grouped.computeIfAbsent(sectionType, ignored -> new StringBuilder())
                    .append(fragment.title())
                    .append("\n")
                    .append(fragment.content())
                    .append("\n\n");
            references.putIfAbsent(
                    sectionType,
                    new ContextReference(
                            "knowledgeFragments",
                            "fragment",
                            fragment.fragmentId(),
                            fragment.fragmentType().name(),
                            ContextSectionPriorities.priority(sectionType)));
            sources.putIfAbsent(sectionType, fragment.sourceDocument());
        }

        List<ResolvedContextSection> sections = new ArrayList<>();
        for (Map.Entry<ContextSectionType, StringBuilder> entry : grouped.entrySet()) {
            ContextSectionType type = entry.getKey();
            ContextSection section = new ContextSection(
                    type,
                    ContextSectionPriorities.priority(type),
                    sources.getOrDefault(type, "knowledgeFragments"),
                    references.get(type),
                    ContextSection.estimateTokensFromContent(entry.getValue().toString()));
            sections.add(ResolvedContextSection.from(section, entry.getValue().toString().trim()));
        }
        sections.sort(java.util.Comparator.comparingInt(ResolvedContextSection::priority)
                .thenComparing(section -> section.type().name()));
        return sections;
    }

    public static ContextSectionType toContextSectionType(KnowledgeFragmentType fragmentType) {
        return switch (fragmentType) {
            case IDENTITY, PERSONALITY, SPEAKING_STYLE, STORYTELLING, HUMOR, SECRET_POLICY, CHARACTER_OPINION ->
                    ContextSectionType.PERSONALITY;
            case WORLD_GEOGRAPHY, WORLD_HISTORY, WORLD_POLITICS, WORLD_SPECIES, WORLD_ECONOMY, WORLD_TRANSPORT, TIMELINE ->
                    ContextSectionType.WORLD_CANON;
            case CHARACTER_PROFILE, CHARACTER_TITLES, CHARACTER_PREFERENCES -> ContextSectionType.CURRENT_CHARACTER;
            case CHARACTER_LOCATION -> ContextSectionType.CURRENT_LOCATION;
            case CHARACTER_RELATIONSHIPS, RELATIONSHIP_TO_BANDAR -> ContextSectionType.RELATIONSHIPS;
            case STORY_SUMMARY -> ContextSectionType.RELEVANT_STORIES;
            case WORKING_MEMORY -> ContextSectionType.WORKING_MEMORY;
            case CONVERSATION -> ContextSectionType.CURRENT_CONVERSATION;
            case UNKNOWN -> ContextSectionType.UNKNOWN;
        };
    }
}
