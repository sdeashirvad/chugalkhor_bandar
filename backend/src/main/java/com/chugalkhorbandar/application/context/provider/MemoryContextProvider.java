package com.chugalkhorbandar.application.context.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionPriorities;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class MemoryContextProvider implements ContextProvider {

    @Override
    public String providerName() {
        return "memory";
    }

    @Override
    public Set<ContextSectionType> supportedTypes() {
        return Set.of(ContextSectionType.LONG_TERM_MEMORY, ContextSectionType.SECRET_MEMORY);
    }

    @Override
    public List<ContextSection> plan(ContextPlannerRequest request, Set<ContextSectionType> selectedTypes) {
        List<ContextSection> sections = new ArrayList<>();
        if (selectedTypes.contains(ContextSectionType.LONG_TERM_MEMORY)) {
            sections.add(section(
                    ContextSectionType.LONG_TERM_MEMORY,
                    providerName(),
                    new ContextReference(providerName(), "memory", "long-term", "unavailable", priority(ContextSectionType.LONG_TERM_MEMORY))));
        }
        if (selectedTypes.contains(ContextSectionType.SECRET_MEMORY)) {
            sections.add(section(
                    ContextSectionType.SECRET_MEMORY,
                    providerName(),
                    new ContextReference(providerName(), "memory", "secret", "unavailable", priority(ContextSectionType.SECRET_MEMORY))));
        }
        return sections;
    }

    @Override
    public ResolvedContextSection resolve(ContextSection section, ContextPlannerRequest request) {
        if (section.type() == ContextSectionType.SECRET_MEMORY) {
            return ResolvedContextSection.from(section, "Secret memory is not available yet.");
        }
        return ResolvedContextSection.from(section, "Long-term memory is not available yet.");
    }

    private static int priority(ContextSectionType type) {
        return ContextSectionPriorities.priority(type);
    }
}
