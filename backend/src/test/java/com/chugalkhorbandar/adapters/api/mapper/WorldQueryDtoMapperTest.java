package com.chugalkhorbandar.adapters.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.api.dto.CharacterSummaryDto;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.application.query.EntityReferenceResolver;
import com.chugalkhorbandar.application.query.StoryQueryService;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import com.chugalkhorbandar.domain.world.runtime.RuntimeStory;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WorldQueryDtoMapperTest {

    private final WorldQueryDtoMapper mapper = new WorldQueryDtoMapper(
            new EntityReferenceResolver(new InMemoryWorldRepositoryProvider(new InMemoryWorldStore())),
            new com.chugalkhorbandar.application.session.CharacterPresenceStore());

    @Test
    void mapsCharacterSummary() {
        RuntimeCharacter character = new RuntimeCharacter(
                "character_hippu_king",
                "Hippu King",
                Map.of("titles", "- King\n- King of 176 Jungles\n", "basicInformation", "| Species | Hippu |"),
                "place_hippu_palace",
                Map.of());

        CharacterSummaryDto dto = mapper.toSummaryDto(character);

        assertThat(dto.id()).isEqualTo("character_hippu_king");
        assertThat(dto.name()).isEqualTo("Hippu King");
        assertThat(dto.species()).isEqualTo("Hippu");
        assertThat(dto.titles()).containsExactly("King", "King of 176 Jungles");
        assertThat(dto.currentPlace()).isEqualTo("place_hippu_palace");
    }

    @Test
    void excludesSecretsFromStorySections() {
        InMemoryWorldStore store = new InMemoryWorldStore();
        store.stories()
                .put(
                        "story-1",
                        new RuntimeStory("story-1", "Origin", Map.of("summary", "Once", "secrets", "hidden"), Map.of()));
        StoryQueryService storyQueryService = new StoryQueryService(
                new InMemoryWorldRepositoryProvider(store),
                new EntityReferenceResolver(new InMemoryWorldRepositoryProvider(store)));

        assertThat(mapper.toDetailsDto(storyQueryService.findDetailsById("story-1")).sections())
                .doesNotContainKey("secrets");
    }
}
