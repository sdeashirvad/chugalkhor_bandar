package com.chugalkhorbandar.bootstrap.typed;

import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class BootstrapTypedLoadingRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapTypedLoadingRunner.class);

    private final BootstrapDocumentRepository documentRepository;
    private final BootstrapTypedLoadingService loadingService;
    private final BootstrapTypedWorldHolder typedWorldHolder;

    public BootstrapTypedLoadingRunner(
            BootstrapDocumentRepository documentRepository,
            BootstrapTypedLoadingService loadingService,
            BootstrapTypedWorldHolder typedWorldHolder) {
        this.documentRepository = documentRepository;
        this.loadingService = loadingService;
        this.typedWorldHolder = typedWorldHolder;
    }

    @Override
    public void run(ApplicationArguments args) {
        BootstrapTypedWorld typedWorld = loadingService.load(documentRepository);
        typedWorldHolder.initialize(typedWorld);

        log.info("Typed Bootstrap Loaded");
        log.info("");
        logCount("Characters", typedWorld.characters().size());
        logCount("Stories", typedWorld.stories().size());
        logCount("Places", typedWorld.places().size());
        logCount("Territories", typedWorld.territories().size());
        logCount("Organizations", typedWorld.organizations().size());
        logCount("Resources", typedWorld.resources().size());
        logCount("Objects", typedWorld.objects().size());
        logCount("Relationships", typedWorld.relationships().size());
        logCount("Laws", typedWorld.laws().size());
        logCount("Customs", typedWorld.customs().size());
        logCount("Glossary", typedWorld.glossaryEntries().size());
        logCount("Prompt Profiles", typedWorld.promptProfiles().size());
        logCount("Chronology", typedWorld.chronologyEntries().size());
        log.info("");
    }

    private void logCount(String label, int count) {
        String dots = ".".repeat(Math.max(1, 18 - label.length()));
        log.info("{}{} {}", label, dots, count);
    }
}
