package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import java.util.List;
import java.util.Optional;

public interface StoryRepository {

    void create(RuntimeStory story);

    boolean exists(String storyId);

    Optional<RuntimeStory> findById(String storyId);

    List<RuntimeStory> findAll(StoryQuery query);

    void linkStory(String storyId, String linkedStoryId, String linkType);
}
