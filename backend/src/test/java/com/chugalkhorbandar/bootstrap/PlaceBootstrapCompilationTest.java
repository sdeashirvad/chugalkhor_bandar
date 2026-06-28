package com.chugalkhorbandar.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilation;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompiler;
import com.chugalkhorbandar.bootstrap.compiler.command.CreatePlaceCommand;
import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentReader;
import com.chugalkhorbandar.bootstrap.typed.BootstrapTypedLoadingService;
import com.chugalkhorbandar.bootstrap.typed.BootstrapTypedReaderRegistry;
import com.chugalkhorbandar.bootstrap.typed.BootstrapTypedWorld;
import com.chugalkhorbandar.bootstrap.typed.reader.PlaceBootstrapReader;
import com.chugalkhorbandar.domain.world.commands.BootstrapToWorldCommandMapper;
import com.chugalkhorbandar.domain.world.runtime.WorldCommandExecutor;
import com.chugalkhorbandar.domain.world.runtime.WorldRuntime;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlaceBootstrapCompilationTest {

    @Test
    void bootstrapPlacesCompileIntoRuntimeWorld() throws Exception {
        Path bootstrapRoot = Path.of("..", "bootstrap").toAbsolutePath().normalize();
        var document = new BootstrapDocumentReader().read(bootstrapRoot, bootstrapRoot.resolve("places.md"));

        PlaceBootstrapReader placeReader = new PlaceBootstrapReader();
        BootstrapTypedWorld.Builder builder = BootstrapTypedWorld.builder();
        placeReader.readAll(document).forEach(builder::addPlace);
        BootstrapTypedWorld typedWorld = builder.build();

        assertThat(typedWorld.places()).hasSizeGreaterThan(10);
        assertThat(typedWorld.places()).extracting(spec -> spec.id()).contains("place_hippu_palace");

        BootstrapCompilation compilation = new BootstrapCompiler().compile(typedWorld);
        CreatePlaceCommand hippuPalace = compilation.commands().stream()
                .filter(CreatePlaceCommand.class::isInstance)
                .map(CreatePlaceCommand.class::cast)
                .filter(command -> "place_hippu_palace".equals(command.placeId()))
                .findFirst()
                .orElseThrow();

        assertThat(hippuPalace.sections()).containsEntry("locatedIn", "Home Jungle");

        WorldRuntime runtime = WorldCommandExecutor.createDefault()
                .execute(new BootstrapToWorldCommandMapper().map(compilation));

        assertThat(runtime.state().places()).containsKey("place_hippu_palace");
        assertThat(runtime.state().places().get("place_hippu_palace").sections())
                .containsEntry("locatedIn", "Home Jungle");
    }
}
