package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.requireRuntime;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.StoryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.StoryMapper;
import com.chugalkhorbandar.domain.world.ports.StoryRepository;
import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import java.util.List;
import java.util.Optional;

public final class PostgresStoryRepository implements StoryRepository {

    private final StoryJpaRepository jpa;

    public PostgresStoryRepository(StoryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeStory story) {
        ensureAbsent(jpa, story.id(), "Stories");
        jpa.save(StoryMapper.toEntity(story));
    }

    @Override
    public boolean exists(String storyId) {
        return jpa.existsById(storyId);
    }

    @Override
    public Optional<RuntimeStory> findById(String storyId) {
        return jpa.findById(storyId).map(StoryMapper::toRuntime);
    }

    @Override
    public List<RuntimeStory> findAll(StoryQuery query) {
        return jpa.findAll().stream()
                .map(StoryMapper::toRuntime)
                .filter(story -> matchesParticipant(story, query.participantId()))
                .filter(story -> matchesLinkedStory(story, query.linkedStoryId()))
                .toList();
    }

    @Override
    public void linkStory(String storyId, String linkedStoryId, String linkType) {
        RuntimeStory story = requireRuntime(findById(storyId), storyId, "Stories");
        jpa.save(StoryMapper.toEntity(story.withLinkedStory(linkedStoryId, linkType)));
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
