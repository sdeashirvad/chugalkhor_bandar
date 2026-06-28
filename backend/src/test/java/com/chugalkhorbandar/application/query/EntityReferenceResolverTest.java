package com.chugalkhorbandar.application.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldRepositoryProvider;
import com.chugalkhorbandar.adapters.persistence.memory.InMemoryWorldStore;
import com.chugalkhorbandar.domain.world.runtime.RuntimePlace;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntityReferenceResolverTest {

    private EntityReferenceResolver resolver;
    private InMemoryWorldStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryWorldStore();
        resolver = new EntityReferenceResolver(new InMemoryWorldRepositoryProvider(store));
        seedWorld();
    }

    @Test
    void resolvesTerritoryForNestedPlaceViaParentCapital() {
        RuntimePlace hippuPalace = store.places().get("place_hippu_palace");

        var territory = resolver.resolveTerritoryForPlace(hippuPalace);

        assertThat(territory).isPresent();
        assertThat(territory.get().id()).isEqualTo("territory_hippu_kingdom");
        assertThat(territory.get().name()).isEqualTo("Hippu Kingdom");
    }

    @Test
    void resolvesTerritoryWhenLocatedInIsTerritoryName() {
        RuntimePlace borderOutpost = store.places().get("place_border_outpost");

        var territory = resolver.resolveTerritoryForPlace(borderOutpost);

        assertThat(territory).isPresent();
        assertThat(territory.get().id()).isEqualTo("territory_second_hippu_kingdom");
    }

    private void seedWorld() {
        store.territories().put(
                "territory_hippu_kingdom",
                new RuntimeTerritory(
                        "territory_hippu_kingdom",
                        "Hippu Kingdom",
                        Map.of("capital", "Home Jungle", "ruler", "Hippu King"),
                        null));
        store.territories().put(
                "territory_second_hippu_kingdom",
                new RuntimeTerritory(
                        "territory_second_hippu_kingdom",
                        "Second Hippu Kingdom",
                        Map.of("capital", "Border Jungle", "ruler", "Second Hippu"),
                        null));
        store.places().put(
                "place_home_jungle",
                new RuntimePlace(
                        "place_home_jungle",
                        "Home Jungle",
                        Map.of("type", "Capital Jungle", "description", "Capital of the Hippu Dynasty.")));
        store.places().put(
                "place_hippu_palace",
                new RuntimePlace(
                        "place_hippu_palace",
                        "Hippu Palace (Hippu House)",
                        Map.of("type", "Royal Residence", "locatedIn", "Home Jungle")));
        store.places().put(
                "place_border_outpost",
                new RuntimePlace(
                        "place_border_outpost",
                        "Border Outpost",
                        Map.of("type", "Military Post", "locatedIn", "Second Hippu Kingdom")));
    }
}
