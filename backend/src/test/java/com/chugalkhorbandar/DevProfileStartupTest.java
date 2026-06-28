package com.chugalkhorbandar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.config.PostgresConfigurationException;
import com.chugalkhorbandar.ports.PersistenceProvider;
import com.chugalkhorbandar.ports.PersistenceProviderType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class DevProfileStartupTest {

    @Test
    void applicationStartsWithDevProfile() {
        assertThat(true).isTrue();
    }

    @Autowired
    private PersistenceProvider persistenceProvider;

    @Test
    void repositoryHealthIsAvailable() {
        assertThat(persistenceProvider.getType()).isEqualTo(PersistenceProviderType.IN_MEMORY_H2);
    }
}
