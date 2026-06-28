package com.chugalkhorbandar;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.bootstrap.BootstrapTestFixtures;
import com.chugalkhorbandar.bootstrap.BootstrapValidationException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;

class BootstrapValidationStartupTest {

    @Test
    void failsStartupWhenBootstrapIsInvalid(@TempDir Path tempDir) throws Exception {
        Path bootstrapPath = tempDir.resolve("bootstrap");
        BootstrapTestFixtures.createValidBootstrap(bootstrapPath);
        Files.delete(bootstrapPath.resolve("manifest.yaml"));

        SpringApplication application = new SpringApplication(ChugalkhorBandarApplication.class);
        application.setAdditionalProfiles("dev");

        assertThatThrownBy(() -> application.run("--chugalkhor.bootstrap-folder=" + bootstrapPath))
                .isInstanceOf(BootstrapValidationException.class);
    }
}
