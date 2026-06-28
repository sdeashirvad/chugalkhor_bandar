package com.chugalkhorbandar.application.context.knowledge.provider;

import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentRequest;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface KnowledgeProvider {

    String providerName();

    Set<KnowledgeFragmentType> supportedFragmentTypes();

    List<KnowledgeFragmentRequest> plan(ContextPlannerRequest request, Set<KnowledgeFragmentType> selectedTypes);

    Optional<KnowledgeFragment> resolve(KnowledgeFragmentRequest request, ContextPlannerRequest context);
}
