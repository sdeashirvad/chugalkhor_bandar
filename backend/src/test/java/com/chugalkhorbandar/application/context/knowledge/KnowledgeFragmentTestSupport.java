package com.chugalkhorbandar.application.context.knowledge;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorkingMemoryRepository;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorkingMemoryStore;
import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlanningTrace;
import com.chugalkhorbandar.application.context.knowledge.provider.BandarKnowledgeProvider;
import com.chugalkhorbandar.application.context.knowledge.provider.CharacterKnowledgeProvider;
import com.chugalkhorbandar.application.context.knowledge.provider.ConversationKnowledgeProvider;
import com.chugalkhorbandar.application.context.knowledge.provider.KnowledgeProvider;
import com.chugalkhorbandar.application.context.knowledge.provider.StoryKnowledgeProvider;
import com.chugalkhorbandar.application.context.knowledge.provider.WorkingMemoryKnowledgeProvider;
import com.chugalkhorbandar.application.context.knowledge.provider.WorldKnowledgeProvider;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryBuilder;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryProperties;
import com.chugalkhorbandar.application.memory.working.WorkingMemoryService;
import com.chugalkhorbandar.application.query.EntityReferenceResolver;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import java.util.List;

public final class KnowledgeFragmentTestSupport {

    private KnowledgeFragmentTestSupport() {}

    public static ContextPlan emptyContextPlan() {
        return new ContextPlan(
                List.of(),
                new KnowledgeFragmentPlan(List.of(), 0, new KnowledgeFragmentPlanningTrace(List.of())),
                0,
                new ContextPlanningTrace(List.of()));
    }

    public static List<KnowledgeProvider> knowledgeProviders(WorldRepositoryProvider repositories) {
        EntityReferenceResolver referenceResolver = new EntityReferenceResolver(repositories);
        WorkingMemoryProperties properties = new WorkingMemoryProperties();
        InMemoryWorkingMemoryStore workingMemoryStore = new InMemoryWorkingMemoryStore();
        WorkingMemoryService workingMemoryService = new WorkingMemoryService(
                null, null, new WorkingMemoryBuilder(properties), new InMemoryWorkingMemoryRepository(workingMemoryStore));
        return List.of(
                new BandarKnowledgeProvider(repositories),
                new CharacterKnowledgeProvider(repositories, referenceResolver),
                new WorldKnowledgeProvider(repositories),
                new ConversationKnowledgeProvider(properties),
                new WorkingMemoryKnowledgeProvider(workingMemoryService),
                new StoryKnowledgeProvider(repositories));
    }

    public static KnowledgeFragmentPlanner fragmentPlanner(WorldRepositoryProvider repositories) {
        return new KnowledgeFragmentPlanner(new KnowledgeFragmentSelector(), knowledgeProviders(repositories));
    }

    public static KnowledgeFragmentResolver fragmentResolver(WorldRepositoryProvider repositories) {
        return new KnowledgeFragmentResolver(knowledgeProviders(repositories), new KnowledgeFragmentRegistry());
    }
}
