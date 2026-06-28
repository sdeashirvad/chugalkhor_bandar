package com.chugalkhorbandar.bootstrap;

import com.chugalkhorbandar.config.ChugalkhorProperties;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class BootstrapFolderValidator implements ApplicationRunner {

    private final ChugalkhorProperties properties;

    public BootstrapFolderValidator(ChugalkhorProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        Path bootstrapPath = Path.of(properties.getBootstrapFolder()).toAbsolutePath().normalize();
        if (!Files.isDirectory(bootstrapPath)) {
            throw new BootstrapFolderMissingException(
                    "Bootstrap folder not found at: " + bootstrapPath
                            + ". Configure chugalkhor.bootstrap-folder or ensure the directory exists.");
        }
    }
}
