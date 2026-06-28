package com.chugalkhorbandar.application.prompt.profile;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.context.ContextPlan;
import com.chugalkhorbandar.application.context.ContextPlannerRequest;
import com.chugalkhorbandar.application.context.ContextPlanningTrace;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlan;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPlanningTrace;
import com.chugalkhorbandar.application.context.resolver.ResolvedContext;
import com.chugalkhorbandar.config.PromptProfileProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextProfileSelectorTest {

    private ContextProfileSelector selector;

    @BeforeEach
    void setUp() {
        selector = new ContextProfileSelector(new ContextProfileCatalog(new PromptProfileProperties()));
    }

    @Test
    void selectsLocationQueryForWhere() {
        ProfileSelection selection = selector.select(request("Where am I?"), emptyPlan(), resolved(10));

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.LOCATION_QUERY);
        assertThat(selection.reason()).contains("where");
    }

    @Test
    void selectsIdentityQueryForWhoAmI() {
        ProfileSelection selection = selector.select(request("Who am I?"), emptyPlan(), resolved(10));

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.IDENTITY_QUERY);
        assertThat(selection.reason()).contains("identity");
    }

    @Test
    void selectsCharacterQueryForWho() {
        ProfileSelection selection = selector.select(request("Who is the king?"), emptyPlan(), resolved(10));

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.CHARACTER_QUERY);
    }

    @Test
    void selectsStoryQueryForStoryKeyword() {
        ProfileSelection selection = selector.select(request("Tell me a story"), emptyPlan(), resolved(10));

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.STORY_QUERY);
    }

    @Test
    void selectsMemoryQueryForRemember() {
        ProfileSelection selection = selector.select(request("Do you remember me?"), emptyPlan(), resolved(10));

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.MEMORY_QUERY);
    }

    @Test
    void selectsRelationshipQueryForKingMention() {
        ProfileSelection selection = selector.select(request("Tell me about the king"), emptyPlan(), resolved(10));

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.RELATIONSHIP_QUERY);
    }

    @Test
    void fallsBackToGeneralChat() {
        ProfileSelection selection = selector.select(request("Hello Bandar"), emptyPlan(), resolved(10));

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.GENERAL_CHAT);
    }

    @Test
    void selectsUnknownForBlankMessage() {
        ProfileSelection selection = selector.select(request("   "), emptyPlan(), resolved(0));

        assertThat(selection.profile().type()).isEqualTo(ContextProfileType.UNKNOWN);
    }

    private static ContextPlannerRequest request(String latestMessage) {
        return new ContextPlannerRequest(null, null, null, latestMessage, null);
    }

    private static ContextPlan emptyPlan() {
        return new ContextPlan(
                java.util.List.of(),
                new KnowledgeFragmentPlan(java.util.List.of(), 0, new KnowledgeFragmentPlanningTrace(java.util.List.of())),
                0,
                new ContextPlanningTrace(java.util.List.of()));
    }

    private static ResolvedContext resolved(int tokens) {
        return new ResolvedContext(java.util.List.of(), java.util.List.of(), tokens);
    }
}
