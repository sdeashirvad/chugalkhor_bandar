package com.chugalkhorbandar.bootstrap;

import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentReader;
import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentRepository;
import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.model.BootstrapFile;
import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BootstrapDocumentLoader {

    private final BootstrapDocumentReader documentReader;

    public BootstrapDocumentLoader() {
        this.documentReader = new BootstrapDocumentReader();
    }

    public List<BootstrapDocument> loadAll(BootstrapWorld world) {
        List<BootstrapDocument> documents = new ArrayList<>();
        for (BootstrapFile file : world.markdownFiles()) {
            if (file.metadata().isEmpty()) {
                continue;
            }
            try {
                documents.add(documentReader.read(world.rootPath(), file.filePath()));
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to load document: " + file.filePath(), e);
            }
        }
        return documents;
    }

    public void loadIntoRepository(BootstrapWorld world, BootstrapDocumentRepository repository) {
        repository.storeAll(loadAll(world));
    }
}
