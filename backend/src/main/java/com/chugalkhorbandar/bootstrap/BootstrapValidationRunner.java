package com.chugalkhorbandar.bootstrap;

import com.chugalkhorbandar.config.ChugalkhorProperties;
import com.chugalkhorbandar.bootstrap.model.ValidationReport;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class BootstrapValidationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapValidationRunner.class);

    private final ChugalkhorProperties properties;
    private final BootstrapValidationService validationService;
    private final BootstrapContextHolder contextHolder;

    public BootstrapValidationRunner(
            ChugalkhorProperties properties,
            BootstrapValidationService validationService,
            BootstrapContextHolder contextHolder) {
        this.properties = properties;
        this.validationService = validationService;
        this.contextHolder = contextHolder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Path bootstrapPath = Path.of(properties.getBootstrapFolder()).toAbsolutePath().normalize();

        log.info("Loading Bootstrap...");
        log.info("");

        BootstrapValidationResult result = validationService.validate(bootstrapPath);
        ValidationReport report = result.report();

        logManifestStatus(report);
        logCount("Characters", report.characterCount());
        logCount("Stories", report.storyCount());
        logCount("Timeline", report.chronologyCount());
        log.info("");

        if (!report.isValid()) {
            log.error("Validation ......... FAILED");
            log.error("");
            log.error(report.toSummary());
            throw new BootstrapValidationException(report);
        }

        contextHolder.initialize(new BootstrapContext(result.world(), report));
        log.info("Validation ......... PASSED");
        log.info("");
    }

    private void logManifestStatus(ValidationReport report) {
        String status = report.manifestValid() ? "OK" : "FAIL";
        log.info("Manifest ............ {}", status);
    }

    private void logCount(String label, int count) {
        String dots = ".".repeat(Math.max(1, 18 - label.length()));
        log.info("{}{} {}", label, dots, count);
    }
}
