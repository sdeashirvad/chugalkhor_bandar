package com.chugalkhorbandar.adapters.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.domain.world.ports.CharacterRepository;
import com.chugalkhorbandar.domain.world.ports.RelationshipRepository;
import com.chugalkhorbandar.domain.world.ports.StoryRepository;
import com.chugalkhorbandar.domain.world.ports.TimelineRepository;
import com.chugalkhorbandar.domain.world.ports.WorldPersistenceService;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.WorldUnitOfWork;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTimelineEntry;
import com.chugalkhorbandar.domain.world.runtime.WorldRuntime;
import com.chugalkhorbandar.domain.world.runtime.WorldState;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class RepositoryContractTestBase {

    protected WorldRepositoryProvider provider;
    protected WorldPersistenceService persistenceService;

    @BeforeEach
    void setUpRepositories() {
        provider = createProvider();
        persistenceService = createPersistenceService(provider);
    }

    protected abstract WorldRepositoryProvider createProvider();

    protected abstract WorldPersistenceService createPersistenceService(WorldRepositoryProvider provider);

    @Test
    void createsAndFindsCharacter() {
        CharacterRepository characters = provider.characters();
        RuntimeCharacter character = new RuntimeCharacter(
                "char-1", "Hippu King", Map.of("summary", "Ruler"), null, Map.of("food", "mango"));

        characters.create(character);

        assertThat(characters.exists("char-1")).isTrue();
        assertThat(characters.findById("char-1")).contains(character);
        assertThat(characters.findAll(CharacterQuery.all())).hasSize(1);
    }

    @Test
    void updatesCharacterTitleAndPreference() {
        CharacterRepository characters = provider.characters();
        characters.create(new RuntimeCharacter("char-1", "Alpha", Map.of(), null, Map.of()));

        characters.assignTitle("char-1", "King Alpha");
        characters.changePreference("char-1", "food", "banana");

        RuntimeCharacter updated = characters.findById("char-1").orElseThrow();
        assertThat(updated.title()).isEqualTo("King Alpha");
        assertThat(updated.preferences()).containsEntry("food", "banana");
    }

    @Test
    void deletesCharacter() {
        CharacterRepository characters = provider.characters();
        characters.create(new RuntimeCharacter("char-1", "Alpha", Map.of(), null, Map.of()));

        characters.delete("char-1");

        assertThat(characters.exists("char-1")).isFalse();
    }

    @Test
    void rejectsDuplicateCharacterCreation() {
        CharacterRepository characters = provider.characters();
        characters.create(new RuntimeCharacter("dup", "First", Map.of(), null, Map.of()));

        assertThatThrownBy(() -> characters.create(new RuntimeCharacter("dup", "Second", Map.of(), null, Map.of())))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    void linksStoriesAndFindsRelationship() {
        StoryRepository stories = provider.stories();
        stories.create(new RuntimeStory("story-a", "Origin", Map.of(), Map.of()));
        stories.create(new RuntimeStory("story-b", "Sequel", Map.of(), Map.of()));
        stories.linkStory("story-a", "story-b", "sequel");

        assertThat(stories.findById("story-a").orElseThrow().linkedStories())
                .containsEntry("story-b", "sequel");

        RelationshipRepository relationships = provider.relationships();
        relationships.create(new RuntimeRelationship(
                "rel-1", "Alliance", Map.of("characters", "char-a,char-b", "relationshipType", "ally")));

        assertThat(relationships.findByCharacter("char-a")).hasSize(1);
        assertThat(relationships.findBetween("char-a", "char-b")).hasSize(1);
    }

    @Test
    void appendsTimelineEntries() {
        TimelineRepository timeline = provider.timeline();
        RuntimeTimelineEntry entry = new RuntimeTimelineEntry(
                "entry-1", "world_timeline", "Ancient Era", List.of(), Map.of("Ancient Era", "events"));

        timeline.append(entry);

        assertThat(timeline.exists("entry-1")).isTrue();
        assertThat(timeline.latest()).isPresent();
        assertThat(timeline.findAll(com.chugalkhorbandar.domain.world.ports.query.TimelineQuery.all()))
                .hasSize(1);
    }

    @Test
    void persistsWorldRuntimeThroughUnitOfWork() {
        WorldState state = WorldState.empty()
                .addCharacter(new RuntimeCharacter("char-1", "Alpha", Map.of(), null, Map.of()))
                .addStory(new RuntimeStory("story-1", "Origin", Map.of("summary", "Once"), Map.of()));
        WorldRuntime runtime = new WorldRuntime(
                state,
                new com.chugalkhorbandar.domain.world.runtime.WorldExecutionReport(
                        2, 1, List.of(), List.of(), state.statistics(), true));

        WorldUnitOfWork unitOfWork = persistenceService.beginUnitOfWork();
        unitOfWork.begin();
        persistenceService.persist(runtime, unitOfWork);
        unitOfWork.commit();

        assertThat(provider.characters().exists("char-1")).isTrue();
        assertThat(provider.stories().exists("story-1")).isTrue();
    }

    @Test
    void rollsBackTransactionOnFailure() {
        WorldState state = WorldState.empty()
                .addCharacter(new RuntimeCharacter("char-rollback", "Beta", Map.of(), null, Map.of()));
        WorldRuntime runtime = new WorldRuntime(
                state,
                new com.chugalkhorbandar.domain.world.runtime.WorldExecutionReport(
                        1, 1, List.of(), List.of(), state.statistics(), true));

        WorldUnitOfWork unitOfWork = persistenceService.beginUnitOfWork();
        unitOfWork.begin();
        persistenceService.persist(runtime, unitOfWork);
        unitOfWork.rollback();

        assertThat(provider.characters().exists("char-rollback")).isFalse();
    }
}
