package com.chugalkhorbandar.bootstrap.typed.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.typed.TypedBootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.typed.TypedReaderException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CharacterBootstrapReaderTest {

    private final CharacterBootstrapReader reader = new CharacterBootstrapReader();

    @Test
    void readsCharacterSpec(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        TypedBootstrapTestFixtures.enrichForTypedLoading(tempDir);

        var document = TypedBootstrapTestFixtures.loadDocument(tempDir, "characters/hero.md");
        var spec = reader.read(document);

        assertThat(spec.id()).isEqualTo("character_hero");
        assertThat(spec.summary()).isEqualTo("A brave hero.");
        assertThat(spec.unmappedSections()).isEmpty();
    }

    @Test
    void failsWhenSummaryMissing(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/hero.md"),
                """
                ---
                id: character_hero
                name: Hero
                version: 1.0
                status: ACTIVE
                ---

                # Hero
                """);

        var document = TypedBootstrapTestFixtures.loadDocument(tempDir, "characters/hero.md");

        assertThatThrownBy(() -> reader.read(document)).isInstanceOf(TypedReaderException.class);
    }

    @Test
    void preservesUnknownSections(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/hero.md"),
                """
                ---
                id: character_hero
                name: Hero
                version: 1.0
                status: ACTIVE
                ---

                # Hero

                ## Summary
                Summary text.

                ## Unknown Section
                Extra content.
                """);

        var document = TypedBootstrapTestFixtures.loadDocument(tempDir, "characters/hero.md");
        var spec = reader.read(document);

        assertThat(spec.unmappedSections()).containsKey("Unknown Section");
        assertThat(spec.unmappedSections().get("Unknown Section")).isEqualTo("Extra content.");
    }

    @Test
    void readsCurrentPlaceFromFrontmatter(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/hero.md"),
                """
                ---
                id: character_hero
                name: Hero
                version: 1.0
                status: ACTIVE
                current_place: place_hero_home
                ---

                # Hero

                ## Summary
                A brave hero.
                """);

        var document = TypedBootstrapTestFixtures.loadDocument(tempDir, "characters/hero.md");
        var spec = reader.read(document);

        assertThat(spec.currentPlace()).isEqualTo("place_hero_home");
    }

    @Test
    void ignoresNonPlaceCurrentPlaceValues(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/hero.md"),
                """
                ---
                id: character_hero
                name: Hero
                version: 1.0
                status: ACTIVE
                current_place: Unknown
                ---

                # Hero

                ## Summary
                A brave hero.
                """);

        var document = TypedBootstrapTestFixtures.loadDocument(tempDir, "characters/hero.md");
        var spec = reader.read(document);

        assertThat(spec.currentPlace()).isNull();
    }

    @Test
    void readsHomeTerritoryFromFrontmatter(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        BootstrapTestFixtures.writeMarkdown(
                tempDir.resolve("characters/hero.md"),
                """
                ---
                id: character_hero
                name: Hero
                version: 1.0
                status: ACTIVE
                home_territory: territory_hero_kingdom
                ---

                # Hero

                ## Summary
                A brave hero.
                """);

        var document = TypedBootstrapTestFixtures.loadDocument(tempDir, "characters/hero.md");
        var spec = reader.read(document);

        assertThat(spec.homeTerritory()).isEqualTo("territory_hero_kingdom");
    }
}
