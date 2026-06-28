package com.chugalkhorbandar.application.query;

import com.chugalkhorbandar.domain.world.ports.StoryRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.StoryQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class StoryQueryService {

    private final StoryRepository stories;
    private final EntityReferenceResolver referenceResolver;

    public StoryQueryService(WorldRepositoryProvider repositoryProvider, EntityReferenceResolver referenceResolver) {
        this.stories = repositoryProvider.stories();
        this.referenceResolver = referenceResolver;
    }

    public List<RuntimeStory> findAll() {
        return stories.findAll(StoryQuery.all()).stream()
                .sorted(Comparator.comparing(RuntimeStory::title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public RuntimeStory findById(String id) {
        return stories.findById(id).orElseThrow(() -> new ResourceNotFoundException("Story", id));
    }

    public StoryDetailsView findDetailsById(String id) {
        RuntimeStory story = findById(id);
        Set<EntityReferenceResolver.ResolvedReference> participants = new LinkedHashSet<>();
        participants.addAll(referenceResolver.resolveListItems(
                story.sections().get("participants"), EntityReferenceResolver.ReferenceType.CHARACTER));
        participants.addAll(referenceResolver.resolveListItems(
                story.sections().get("linkedCharacters"), EntityReferenceResolver.ReferenceType.CHARACTER));

        Set<EntityReferenceResolver.ResolvedReference> storyPlaces = new LinkedHashSet<>();
        storyPlaces.addAll(referenceResolver.resolveListItems(
                story.sections().get("majorPlaces"), EntityReferenceResolver.ReferenceType.PLACE));
        storyPlaces.addAll(referenceResolver.resolveListItems(
                story.sections().get("linkedPlaces"), EntityReferenceResolver.ReferenceType.PLACE));

        return new StoryDetailsView(story, List.copyOf(participants), List.copyOf(storyPlaces));
    }

    public record StoryDetailsView(
            RuntimeStory story,
            List<EntityReferenceResolver.ResolvedReference> participants,
            List<EntityReferenceResolver.ResolvedReference> places) {}
}
