package com.chugalkhorbandar.application.context.knowledge;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KnowledgeFragmentRegistryTest {

    private KnowledgeFragmentRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new KnowledgeFragmentRegistry();
    }

    @Test
    void lookupByTypeTagEntityAndSource() {
        KnowledgeFragment location = KnowledgeFragment.of(
                KnowledgeFragmentType.CHARACTER_LOCATION,
                "Current Location",
                "Hippu King lives in Hippu Palace.",
                "place_hippu_palace",
                "details",
                Set.of("location", "hippu"),
                1.0);
        KnowledgeFragment identity = KnowledgeFragment.of(
                KnowledgeFragmentType.IDENTITY,
                "Identity",
                "I am Bandar.",
                "prompt_bandar_personality",
                "identity",
                Set.of(),
                1.0);

        registry.registerAll(java.util.List.of(location, identity));

        assertThat(registry.findByType(KnowledgeFragmentType.CHARACTER_LOCATION)).containsExactly(location);
        assertThat(registry.findByTag("location")).containsExactly(location);
        assertThat(registry.findBySource("place_hippu_palace")).containsExactly(location);
        assertThat(registry.findById(location.fragmentId())).contains(location);
        assertThat(registry.allFragments()).containsExactlyInAnyOrder(location, identity);
    }
}
