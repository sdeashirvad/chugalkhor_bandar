package com.chugalkhorbandar.bootstrap.typed.reader;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentReader;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PlaceBootstrapReaderTest {

    private final PlaceBootstrapReader reader = new PlaceBootstrapReader();
    private final BootstrapDocumentReader documentReader = new BootstrapDocumentReader();

    @Test
    void readsAllPlacesFromBootstrapCanon() throws Exception {
        Path bootstrapRoot = Path.of("..", "bootstrap").toAbsolutePath().normalize();
        Path placesFile = bootstrapRoot.resolve("places.md");
        assertThat(placesFile).exists();

        var document = documentReader.read(bootstrapRoot, placesFile);
        var places = reader.readAll(document);

        assertThat(places).extracting(spec -> spec.id()).contains(
                "place_home_jungle",
                "place_hippu_palace",
                "place_hippu_court",
                "place_giraffe_jungle_school",
                "place_border_jungle",
                "place_ancient_rabbitu_jungle",
                "place_human_settlement");
    }

    @Test
    void hippuPalaceIncludesLocatedInHomeJungle() throws Exception {
        Path bootstrapRoot = Path.of("..", "bootstrap").toAbsolutePath().normalize();
        var document = documentReader.read(bootstrapRoot, bootstrapRoot.resolve("places.md"));

        var hippuPalace = reader.readAll(document).stream()
                .filter(place -> "place_hippu_palace".equals(place.id()))
                .findFirst()
                .orElseThrow();

        assertThat(hippuPalace.title()).isEqualTo("Hippu Palace (Hippu House)");
        assertThat(hippuPalace.locatedIn()).isEqualTo("Home Jungle");
        assertThat(hippuPalace.sourceDocumentId()).isEqualTo("places");
    }
}
