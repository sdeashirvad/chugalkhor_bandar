package com.chugalkhorbandar.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CharacterQueryServiceTest {

    private final InMemoryWorldStore store = new InMemoryWorldStore();
    private final CharacterQueryService service = new CharacterQueryService(
            new InMemoryWorldRepositoryProvider(store), new EntityReferenceResolver(new InMemoryWorldRepositoryProvider(store)));

    @BeforeEach
    void seedCharacters() {
        store.characters()
                .put(
                        "character_zeta",
                        new RuntimeCharacter(
                                "character_zeta",
                                "Zeta",
                                Map.of("titles", "- Zeta\n", "basicInformation", "| Species | Hippu |"),
                                "place_b",
                                Map.of()));
        store.characters()
                .put(
                        "character_alpha",
                        new RuntimeCharacter(
                                "character_alpha",
                                "Alpha",
                                Map.of("titles", "- Alpha\n", "basicInformation", "| Species | Rabbitu |"),
                                "place_a",
                                Map.of("food", "carrot")));
    }

    @Test
    void findAllSortsByName() {
        assertThat(service.findAll()).extracting(RuntimeCharacter::id).containsExactly("character_alpha", "character_zeta");
    }

    @Test
    void findByIdReturnsCharacter() {
        assertThat(service.findById("character_alpha").title()).isEqualTo("Alpha");
    }

    @Test
    void findByTitleFiltersCharacters() {
        assertThat(service.findByTitle("alp")).extracting(RuntimeCharacter::id).containsExactly("character_alpha");
    }

    @Test
    void findByPlaceFiltersCharacters() {
        assertThat(service.findByPlace("place_a")).extracting(RuntimeCharacter::id).containsExactly("character_alpha");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        assertThatThrownBy(() -> service.findById("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
