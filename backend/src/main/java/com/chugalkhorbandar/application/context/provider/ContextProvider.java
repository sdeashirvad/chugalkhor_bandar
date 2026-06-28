package com.chugalkhorbandar.application.context.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSection;
import com.chugalkhorbandar.application.context.ContextSectionPriorities;
import com.chugalkhorbandar.application.context.ContextSectionType;
import com.chugalkhorbandar.application.context.resolver.ResolvedContextSection;
import java.util.List;
import java.util.Set;

public interface ContextProvider {

    Set<ContextSectionType> supportedTypes();

    String providerName();

    List<ContextSection> plan(ContextPlannerRequest request, Set<ContextSectionType> selectedTypes);

    default boolean supports(ContextReference reference) {
        return providerName().equals(reference.provider());
    }

    ResolvedContextSection resolve(ContextSection section, ContextPlannerRequest request);

    default ContextSection section(ContextSectionType type, String source, ContextReference reference) {
        return new ContextSection(
                type,
                ContextSectionPriorities.priority(type),
                source,
                reference,
                ContextSection.estimateTokens(reference));
    }
}
