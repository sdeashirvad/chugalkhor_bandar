package com.chugalkhorbandar.bootstrap.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapDocumentLoader;
import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.scanner.BootstrapScanner;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import com.chugalkhorbandar.bootstrap.parser.ManifestParser;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BootstrapDocumentRepositoryTest {

    private final BootstrapScanner scanner =
            new BootstrapScanner(new ManifestParser(), new FrontmatterParser());
    private final BootstrapDocumentLoader loader = new BootstrapDocumentLoader();

    @Test
    void storesAndQueriesDocuments(@TempDir Path tempDir) throws Exception {
        BootstrapTestFixtures.createValidBootstrap(tempDir);
        var world = scanner.scan(tempDir);
        var repository = new BootstrapDocumentRepository();

        loader.loadIntoRepository(world, repository);

        assertThat(repository.countAll()).isEqualTo(5);
        assertThat(repository.findById("character_hero")).isPresent();
        assertThat(repository.findByType(DocumentType.CHARACTER)).hasSize(1);
        assertThat(repository.findByType(DocumentType.STORY)).hasSize(1);
        assertThat(repository.findAll()).hasSize(5);
    }
}
