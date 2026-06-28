package com.chugalkhorbandar.adapters.persistence.postgres;

import com.chugalkhorbandar.adapters.persistence.RepositoryContractTestBase;
import com.chugalkhorbandar.domain.world.ports.WorldPersistenceService;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@EnabledIf("com.chugalkhorbandar.adapters.persistence.postgres.DockerTestSupport#isDockerAvailable")
@SpringBootTest(properties = "spring.profiles.active=postgres-dev")
@ActiveProfiles("postgres-dev")
class PostgresRepositoryContractTest extends RepositoryContractTestBase {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void registerDataSource(DynamicPropertyRegistry registry) {
        registry.add("POSTGRES_HOST", POSTGRES::getHost);
        registry.add("POSTGRES_PORT", () -> String.valueOf(POSTGRES.getMappedPort(5432)));
        registry.add("POSTGRES_DB", POSTGRES::getDatabaseName);
        registry.add("POSTGRES_USER", POSTGRES::getUsername);
        registry.add("POSTGRES_PASSWORD", POSTGRES::getPassword);
        registry.add("POSTGRES_SSLMODE", () -> "disable");
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private WorldRepositoryProvider worldRepositoryProvider;

    @Autowired
    private WorldPersistenceService worldPersistenceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute(
                """
                TRUNCATE TABLE
                  glossary_entries, customs, laws, world_rules, canon_entries,
                  prompt_profiles, timeline_entries, objects, resources, organizations,
                  relationships, stories, places, territories, characters
                CASCADE
                """);
    }

    @Override
    protected WorldRepositoryProvider createProvider() {
        return worldRepositoryProvider;
    }

    @Override
    protected WorldPersistenceService createPersistenceService(WorldRepositoryProvider provider) {
        return worldPersistenceService;
    }

    @Test
    void postgresContainerIsRunning() {
        org.assertj.core.api.Assertions.assertThat(POSTGRES.isRunning()).isTrue();
    }
}
