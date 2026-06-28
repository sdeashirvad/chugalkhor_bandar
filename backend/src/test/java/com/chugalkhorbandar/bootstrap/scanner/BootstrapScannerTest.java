package com.chugalkhorbandar.bootstrap.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BootstrapScannerTest {

    private final BootstrapScanner scanner =
            new BootstrapScanner(new ManifestParser(), new FrontmatterParser());

    @Test
    void discoversBootstrapFiles(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);

        var world = scanner.scan(tempDir);

        assertThat(world.manifest()).isPresent();
        assertThat(world.manifest().get().worldId()).isEqualTo("test_world");
        assertThat(world.characters()).hasSize(1);
        assertThat(world.stories()).hasSize(1);
        assertThat(world.prompts()).hasSize(1);
        assertThat(world.chronology()).hasSize(1);
        assertThat(world.references()).hasSize(1);
    }
}
