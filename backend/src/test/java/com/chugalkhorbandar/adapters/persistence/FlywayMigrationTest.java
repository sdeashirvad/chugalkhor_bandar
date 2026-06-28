package com.chugalkhorbandar.adapters.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.profiles.active=dev")
@ActiveProfiles("dev")
class FlywayMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createsSchemaOnStartup() {
        Integer migrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"flyway_schema_history\"", Integer.class);
        assertThat(migrations).isGreaterThan(0);
        assertThat(tableExists("characters")).isTrue();
        assertThat(tableExists("stories")).isTrue();
        assertThat(tableExists("timeline_entries")).isTrue();
        assertThat(tableExists("conversations")).isTrue();
        assertThat(tableExists("conversation_messages")).isTrue();
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = ?",
                Integer.class,
                tableName.toUpperCase());
        return count != null && count > 0;
    }
}
