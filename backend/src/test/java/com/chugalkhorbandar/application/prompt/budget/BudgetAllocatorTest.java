package com.chugalkhorbandar.application.prompt.budget;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.application.llm.ProviderCapabilities;
import com.chugalkhorbandar.application.prompt.ComposedPrompt;
import com.chugalkhorbandar.application.prompt.PromptComposer;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;
import com.chugalkhorbandar.application.prompt.PromptSection;
import com.chugalkhorbandar.application.prompt.PromptSectionType;
import com.chugalkhorbandar.application.prompt.profile.ContextProfile;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileCatalog;
import com.chugalkhorbandar.application.prompt.profile.ContextProfileType;
import com.chugalkhorbandar.config.PromptProfileProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BudgetAllocatorTest {

    private BudgetAllocator allocator;
    private ContextProfile locationProfile;

    @BeforeEach
    void setUp() {
        allocator = new BudgetAllocator(new PromptProfileProperties());
        locationProfile = new ContextProfileCatalog(new PromptProfileProperties())
                .profile(ContextProfileType.LOCATION_QUERY);
    }

    @Test
    void preservesRequiredSectionsWhenBudgetIsTight() {
        ComposedPrompt composed = largePrompt();
        ProviderCapabilities capabilities = new ProviderCapabilities(300, 100, true, true);

        BudgetedPrompt budgeted = allocator.allocate(composed, locationProfile, capabilities);

        assertThat(budgeted.sections()).extracting(section -> section.section().sectionType())
                .contains(
                        PromptSectionType.CURRENT_USER,
                        PromptSectionType.USER_MESSAGE,
                        PromptSectionType.INSTRUCTIONS);
    }

    @Test
    void dropsReducedOptionalSectionsForLocationProfile() {
        ComposedPrompt composed = largePrompt();
        ProviderCapabilities capabilities = new ProviderCapabilities(250, 50, true, true);

        BudgetedPrompt budgeted = allocator.allocate(composed, locationProfile, capabilities);

        assertThat(budgeted.droppedSections()).extracting(DroppedSection::sectionType)
                .anyMatch(type -> type == PromptSectionType.WORLD_FACTS || type == PromptSectionType.RELEVANT_STORIES);
    }

    @Test
    void truncatesSectionsWhenNeeded() {
        ComposedPrompt composed = new ComposedPrompt(List.of(
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?"),
                PromptSection.of(
                        PromptSectionType.INSTRUCTIONS,
                        "Instructions",
                        true,
                        ("Answer only using the provided world knowledge. " + "Extra rule. ").repeat(120))));
        ProviderCapabilities capabilities = new ProviderCapabilities(120, 20, true, true);

        BudgetedPrompt budgeted = allocator.allocate(composed, locationProfile, capabilities);

        assertThat(budgeted.sections()).anyMatch(BudgetedPromptSection::truncated);
        assertThat(budgeted.totalPromptTokens()).isLessThanOrEqualTo(capabilities.availablePromptTokens());
        assertThat(budgeted.remainingBudget()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void adaptsToProviderCapabilities() {
        ComposedPrompt composed = samplePrompt();
        ProviderCapabilities capabilities = new ProviderCapabilities(8192, 1024, true, true);

        BudgetedPrompt budgeted = allocator.allocate(composed, locationProfile, capabilities);

        assertThat(budgeted.budget().maxContextTokens()).isEqualTo(8192);
        assertThat(budgeted.budget().reservedOutputTokens()).isEqualTo(1024);
        assertThat(budgeted.budget().totalAvailableTokens()).isEqualTo(7168);
        assertThat(budgeted.droppedSections()).isEmpty();
    }

    @Test
    void dropsIndividualFragmentsIndependently() {
        ComposedPrompt composed = new ComposedPrompt(List.of(
                PromptSection.fromFragment(
                        PromptSectionType.PERSONALITY,
                        "Identity",
                        10,
                        true,
                        40,
                        "I am Bandar.",
                        "source:identity:IDENTITY",
                        KnowledgeFragmentType.IDENTITY),
                PromptSection.fromFragment(
                        PromptSectionType.CURRENT_LOCATION,
                        "Location",
                        60,
                        false,
                        30,
                        "Hippu Palace",
                        "place:details:CHARACTER_LOCATION",
                        KnowledgeFragmentType.CHARACTER_LOCATION),
                PromptSection.fromFragment(
                        PromptSectionType.RELEVANT_STORIES,
                        "Story",
                        70,
                        false,
                        200,
                        "Story detail ".repeat(80),
                        "story:summary:STORY_SUMMARY",
                        KnowledgeFragmentType.STORY_SUMMARY),
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?"),
                PromptSection.of(
                        PromptSectionType.INSTRUCTIONS,
                        "Instructions",
                        true,
                        PromptComposer.DEFAULT_INSTRUCTION)));
        ProviderCapabilities capabilities = new ProviderCapabilities(320, 50, true, true);

        BudgetedPrompt budgeted = allocator.allocate(composed, locationProfile, capabilities);

        assertThat(budgeted.droppedSections()).extracting(DroppedSection::sectionType)
                .contains(PromptSectionType.RELEVANT_STORIES);
        assertThat(budgeted.sections().stream().map(section -> section.section().fragmentId()))
                .anyMatch(id -> id.contains("CHARACTER_LOCATION"));
    }

    private static ComposedPrompt samplePrompt() {
        return new ComposedPrompt(List.of(
                PromptSection.of(PromptSectionType.CURRENT_LOCATION, "Current Location", false, "Hippu Palace"),
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?"),
                PromptSection.of(
                        PromptSectionType.INSTRUCTIONS,
                        "Instructions",
                        true,
                        PromptComposer.DEFAULT_INSTRUCTION)));
    }

    private static ComposedPrompt largePrompt() {
        return new ComposedPrompt(List.of(
                PromptSection.of(PromptSectionType.CURRENT_USER, "Current User", true, "Hippu King"),
                PromptSection.of(PromptSectionType.PERSONALITY, "Personality", true, "Cheerful bandar"),
                PromptSection.of(PromptSectionType.WORLD_FACTS, "World Facts", true, "Canon ".repeat(80)),
                PromptSection.of(PromptSectionType.CURRENT_LOCATION, "Current Location", false, "Hippu Palace"),
                PromptSection.of(PromptSectionType.RELEVANT_STORIES, "Relevant Stories", false, "Story ".repeat(80)),
                PromptSection.of(PromptSectionType.CURRENT_CONVERSATION, "Conversation", true, "USER: Hi"),
                PromptSection.of(PromptSectionType.USER_MESSAGE, "User Message", true, "Where am I?"),
                PromptSection.of(
                        PromptSectionType.INSTRUCTIONS,
                        "Instructions",
                        true,
                        PromptComposer.DEFAULT_INSTRUCTION)));
    }
}
