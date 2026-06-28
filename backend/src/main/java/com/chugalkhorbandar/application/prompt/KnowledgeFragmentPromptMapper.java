package com.chugalkhorbandar.application.prompt;

import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragment;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentPriorities;
import com.chugalkhorbandar.application.context.knowledge.KnowledgeFragmentType;

public final class KnowledgeFragmentPromptMapper {

    private KnowledgeFragmentPromptMapper() {}

    public static PromptSectionType toPromptSectionType(KnowledgeFragmentType fragmentType) {
        return switch (fragmentType) {
            case IDENTITY, PERSONALITY, SPEAKING_STYLE, STORYTELLING, HUMOR, SECRET_POLICY, CHARACTER_OPINION ->
                    PromptSectionType.PERSONALITY;
            case RELATIONSHIP_TO_BANDAR -> PromptSectionType.RELATIONSHIP_TO_BANDAR;
            case WORLD_GEOGRAPHY, WORLD_HISTORY, WORLD_POLITICS, WORLD_SPECIES, WORLD_ECONOMY, WORLD_TRANSPORT, TIMELINE ->
                    PromptSectionType.WORLD_FACTS;
            case CHARACTER_PROFILE, CHARACTER_TITLES, CHARACTER_PREFERENCES -> PromptSectionType.CURRENT_CHARACTER;
            case CHARACTER_LOCATION -> PromptSectionType.CURRENT_LOCATION;
            case CHARACTER_RELATIONSHIPS -> PromptSectionType.RELATIONSHIPS;
            case STORY_SUMMARY -> PromptSectionType.RELEVANT_STORIES;
            case WORKING_MEMORY -> PromptSectionType.WORKING_MEMORY;
            case CONVERSATION -> PromptSectionType.CURRENT_CONVERSATION;
            case UNKNOWN -> PromptSectionType.UNKNOWN;
        };
    }

    public static PromptSection toPromptSection(KnowledgeFragment fragment, boolean required) {
        return PromptSection.fromFragment(
                toPromptSectionType(fragment.fragmentType()),
                fragment.title(),
                KnowledgeFragmentPriorities.priority(fragment.fragmentType()),
                required,
                fragment.estimatedTokens(),
                fragment.content(),
                fragment.fragmentId(),
                fragment.fragmentType());
    }
}
