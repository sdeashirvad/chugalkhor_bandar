package com.chugalkhorbandar.application.context.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PersonalityContextProvider implements ContextProvider {

    private static final String BANDAR_PERSONALITY_ID = "prompt_bandar_personality";

    private final WorldRepositoryProvider repositories;

    public PersonalityContextProvider(WorldRepositoryProvider repositories) {
        this.repositories = repositories;
    }

    @Override
    public String providerName() {
        return "promptProfiles";
    }

    @Override
    public Set<ContextSectionType> supportedTypes() {
        return Set.of(ContextSectionType.PERSONALITY, ContextSectionType.PROMPT_RULES);
    }

    @Override
    public List<ContextSection> plan(ContextPlannerRequest request, Set<ContextSectionType> selectedTypes) {
        List<ContextSection> sections = new ArrayList<>();
        if (selectedTypes.contains(ContextSectionType.PERSONALITY)) {
            sections.add(section(
                    ContextSectionType.PERSONALITY,
                    providerName(),
                    reference("promptProfile", BANDAR_PERSONALITY_ID, "sections")));
        }
        if (selectedTypes.contains(ContextSectionType.PROMPT_RULES)) {
            String rulesId = repositories.worldRules().findAll().stream()
                    .findFirst()
                    .map(rules -> rules.id())
                    .orElse("none");
            sections.add(section(
                    ContextSectionType.PROMPT_RULES,
                    "worldRules",
                    new ContextReference("worldRules", "worldRules", rulesId, "sections", priority(ContextSectionType.PROMPT_RULES))));
        }
        return sections;
    }

    @Override
    public ResolvedContextSection resolve(ContextSection section, ContextPlannerRequest request) {
        if (section.type() == ContextSectionType.PERSONALITY) {
            return repositories.promptProfiles().findById(section.reference().entityId())
                    .map(profile -> ResolvedContextSection.from(section, formatSections(profile.title(), profile.sections())))
                    .orElseGet(() -> ResolvedContextSection.from(section, missing(section.reference().entityId())));
        }
        if (section.reference().entityId().equals("none")) {
            return ResolvedContextSection.from(section, "World rules are not available.");
        }
        return repositories.worldRules().findById(section.reference().entityId())
                .map(rules -> ResolvedContextSection.from(section, formatSections(rules.title(), rules.sections())))
                .orElseGet(() -> ResolvedContextSection.from(section, missing(section.reference().entityId())));
    }

    @Override
    public boolean supports(ContextReference reference) {
        return providerName().equals(reference.provider()) || "worldRules".equals(reference.provider());
    }

    private ContextReference reference(String entityType, String entityId, String attribute) {
        return new ContextReference(providerName(), entityType, entityId, attribute, priority(ContextSectionType.PERSONALITY));
    }

    private static int priority(ContextSectionType type) {
        return com.chugalkhorbandar.application.context.ContextSectionPriorities.priority(type);
    }

    private static String formatSections(String title, Map<String, String> sections) {
        if (sections.isEmpty()) {
            return title;
        }
        return title + "\n" + sections.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue().trim())
                .collect(Collectors.joining("\n"));
    }

    private static String missing(String entityId) {
        return "[missing: " + entityId + "]";
    }
}
