package com.chugalkhorbandar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chugalkhorbandar.config.PostgresConfigurationException;
import com.chugalkhorbandar.ports.PersistenceProvider;
import com.chugalkhorbandar.ports.PersistenceProviderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class ProfileSelectionTest {

    @Autowired
    private PersistenceProvider persistenceProvider;

    @Test
    void devProfileSelectsInMemoryPersistence() {
        assertThat(persistenceProvider.getType()).isEqualTo(PersistenceProviderType.IN_MEMORY_H2);
    }

    @Test
    void postgresDevProfileFailsWithoutDatabaseConfiguration() {
        SpringApplication application = new SpringApplication(ChugalkhorBandarApplication.class);
        application.setAdditionalProfiles("postgres-dev");

        assertThatThrownBy(() -> application.run("--chugalkhor.bootstrap-folder=../bootstrap"))
                .hasRootCauseInstanceOf(PostgresConfigurationException.class);
    }
}
