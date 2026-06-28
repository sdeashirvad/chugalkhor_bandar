package com.chugalkhorbandar.bootstrap;

import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationReport;
import com.chugalkhorbandar.bootstrap.scanner.BootstrapScanner;
import com.chugalkhorbandar.bootstrap.validation.BootstrapValidator;
import java.nio.file.Path;
import org.springframework.stereotype.Service;

@Service
public class BootstrapValidationService {

    private final BootstrapScanner scanner;
    private final BootstrapValidator validator;

    public BootstrapValidationService(BootstrapScanner scanner, BootstrapValidator validator) {
        this.scanner = scanner;
        this.validator = validator;
    }

    public BootstrapValidationResult validate(Path bootstrapRoot) {
        BootstrapWorld world = scanner.scan(bootstrapRoot);
        ValidationReport report = validator.validate(world);
        return new BootstrapValidationResult(world, report);
    }
}
