package com.chugalkhorbandar.application.context.knowledge;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.domain.world.runtime.RuntimePromptProfile;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeFragmentSelectorTest {

    private KnowledgeFragmentSelector selector;

    @BeforeEach
    void setUp() {
        selector = new KnowledgeFragmentSelector();
    }

    @Test
    void selectsIdentityFragmentsForWhoAmI() {
        var selections = selector.select("Who am I?");

        assertThat(selections.keySet()).contains(KnowledgeFragmentType.RELATIONSHIP_TO_BANDAR);
    }

    @Test
    void selectsLocationFragmentsForWhereQuestion() {
        var selections = selector.select("Where am I?");

        assertThat(selections.keySet())
                .contains(
                        KnowledgeFragmentType.IDENTITY,
                        KnowledgeFragmentType.SPEAKING_STYLE,
                        KnowledgeFragmentType.CHARACTER_LOCATION,
                        KnowledgeFragmentType.CHARACTER_PROFILE);
        assertThat(selections.keySet())
                .doesNotContain(
                        KnowledgeFragmentType.WORLD_HISTORY,
                        KnowledgeFragmentType.WORLD_ECONOMY,
                        KnowledgeFragmentType.STORY_SUMMARY);
    }
}
