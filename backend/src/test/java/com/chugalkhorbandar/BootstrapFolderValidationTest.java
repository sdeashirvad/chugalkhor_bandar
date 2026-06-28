package com.chugalkhorbandar;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.bootstrap.BootstrapFolderMissingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

class BootstrapFolderValidationTest {

    @Test
    void failsStartupWhenBootstrapFolderMissing() {
        SpringApplication application = new SpringApplication(ChugalkhorBandarApplication.class);
        application.setAdditionalProfiles("dev");

        assertThatThrownBy(() -> application.run(
                        "--chugalkhor.bootstrap-folder=./nonexistent-bootstrap-folder-test"))
                .isInstanceOf(BootstrapFolderMissingException.class)
                .hasMessageContaining("Bootstrap folder not found");
    }
}
