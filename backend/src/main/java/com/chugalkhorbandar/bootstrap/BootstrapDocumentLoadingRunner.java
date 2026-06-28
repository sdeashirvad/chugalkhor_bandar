package com.chugalkhorbandar.bootstrap;

import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentRepository;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class BootstrapDocumentLoadingRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapDocumentLoadingRunner.class);

    private final BootstrapContextHolder contextHolder;
    private final BootstrapDocumentLoader documentLoader;
    private final BootstrapDocumentRepository documentRepository;

    public BootstrapDocumentLoadingRunner(
            BootstrapContextHolder contextHolder,
            BootstrapDocumentLoader documentLoader,
            BootstrapDocumentRepository documentRepository) {
        this.contextHolder = contextHolder;
        this.documentLoader = documentLoader;
        this.documentRepository = documentRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        var context = contextHolder.getRequired();
        documentLoader.loadIntoRepository(context.getWorld(), documentRepository);

        log.info("Bootstrap Documents Loaded");
        log.info("");
        logCount("Characters", documentRepository.countByType(DocumentType.CHARACTER));
        logCount("Stories", documentRepository.countByType(DocumentType.STORY));
        logCount("Places", documentRepository.countByType(DocumentType.PLACES));
        logCount("Resources", documentRepository.countByType(DocumentType.RESOURCES));
        logCount("Objects", documentRepository.countByType(DocumentType.OBJECTS));
        logCount("Total Documents", documentRepository.countAll());
        log.info("");
    }

    private void logCount(String label, int count) {
        String dots = ".".repeat(Math.max(1, 18 - label.length()));
        log.info("{}{} {}", label, dots, count);
    }
}
