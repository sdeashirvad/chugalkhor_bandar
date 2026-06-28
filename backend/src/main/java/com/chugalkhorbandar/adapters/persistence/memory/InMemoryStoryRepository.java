package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.domain.world.ports.StoryRepository;
import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import java.util.List;
import java.util.Optional;

public final class InMemoryStoryRepository implements StoryRepository {

    private final InMemoryWorldStore store;

    public InMemoryStoryRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeStory story) {
        putUnique(store.stories(), story.id(), story, "Stories");
    }

    @Override
    public boolean exists(String storyId) {
        return store.stories().containsKey(storyId);
    }

    @Override
    public Optional<RuntimeStory> findById(String storyId) {
        return Optional.ofNullable(store.stories().get(storyId));
    }

    @Override
    public List<RuntimeStory> findAll(StoryQuery query) {
        return store.stories().values().stream()
                .filter(story -> matchesParticipant(story, query.participantId()))
                .filter(story -> matchesLinkedStory(story, query.linkedStoryId()))
                .toList();
    }

    @Override
    public void linkStory(String storyId, String linkedStoryId, String linkType) {
        RuntimeStory story = requirePresent(store.stories(), storyId, "Stories");
        store.stories().put(storyId, story.withLinkedStory(linkedStoryId, linkType));
    }

    private static boolean matchesParticipant(RuntimeStory story, String participantId) {
        if (participantId == null) {
            return true;
        }
        return story.sections().values().stream().anyMatch(value -> value.contains(participantId));
    }

    private static boolean matchesLinkedStory(RuntimeStory story, String linkedStoryId) {
        return linkedStoryId == null || story.linkedStories().containsKey(linkedStoryId);
    }
}
