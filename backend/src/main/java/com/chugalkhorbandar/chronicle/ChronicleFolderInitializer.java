package com.chugalkhorbandar.chronicle;

import com.chugalkhorbandar.config.ChugalkhorProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(7)
public class ChronicleFolderInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ChronicleFolderInitializer.class);

    private final ChugalkhorProperties properties;

    public ChronicleFolderInitializer(ChugalkhorProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        Path chroniclePath = Path.of(properties.getChronicleFolder()).toAbsolutePath().normalize();
        if (!Files.exists(chroniclePath)) {
            Files.createDirectories(chroniclePath);
            log.info("Created chronicle folder at: {}", chroniclePath);
        }
    }
}
