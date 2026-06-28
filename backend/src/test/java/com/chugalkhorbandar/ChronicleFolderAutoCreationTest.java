package com.chugalkhorbandar;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

class ChronicleFolderAutoCreationTest {

    @Test
    void createsChronicleFolderWhenMissing(@TempDir Path tempDir) throws Exception {
        Path bootstrapPath = tempDir.resolve("bootstrap");
        Path chroniclePath = tempDir.resolve("chronicles");

        BootstrapTestFixtures.createValidBootstrap(bootstrapPath);
        assertThat(chroniclePath).doesNotExist();

        SpringApplication application = new SpringApplication(ChugalkhorBandarApplication.class);
        application.setAdditionalProfiles("dev");

        try (ConfigurableApplicationContext context = application.run(
                "--chugalkhor.bootstrap-folder=" + bootstrapPath,
                "--chugalkhor.chronicle-folder=" + chroniclePath)) {
            assertThat(chroniclePath).exists().isDirectory();
        }
    }
}
