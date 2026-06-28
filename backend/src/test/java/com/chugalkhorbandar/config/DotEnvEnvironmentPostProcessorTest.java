package com.chugalkhorbandar.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DotEnvEnvironmentPostProcessorTest {

    @Test
    void mapsFlatEnvKeysToSpringPropertyNames() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("LLM_PROVIDER", "groq");
        properties.put("GROQ_MODEL", "llama-3.3-70b-versatile");
        properties.put("GROQ_API_KEY_1", "test-key-1");
        properties.put("POSTGRES_URL", "jdbc:postgresql://db.example.com:5432/postgres?sslmode=require");

        DotEnvLoader.mapEnvToSpringProperties(properties);

        assertThat(properties.get("spring.datasource.url")).isEqualTo(
                "jdbc:postgresql://db.example.com:5432/postgres?sslmode=require");

        assertThat(properties.get("llm.provider")).isEqualTo("groq");
        assertThat(properties.get("llm.model")).isEqualTo("llama-3.3-70b-versatile");
        assertThat(properties.get("groq.api-key-1")).isEqualTo("test-key-1");
    }

    @Test
    void buildsPostgresJdbcUrlFromIndividualEnvVars() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("POSTGRES_HOST", "db.supabase.co");
        properties.put("POSTGRES_PORT", "5432");
        properties.put("POSTGRES_DB", "postgres");
        properties.put("POSTGRES_USER", "postgres");
        properties.put("POSTGRES_PASSWORD", "secret");
        properties.put("POSTGRES_SSLMODE", "require");

        DotEnvLoader.mapEnvToSpringProperties(properties);

        assertThat(properties.get("spring.datasource.url"))
                .isEqualTo("jdbc:postgresql://db.supabase.co:5432/postgres?sslmode=require");
        assertThat(properties.get("spring.datasource.username")).isEqualTo("postgres");
        assertThat(properties.get("spring.datasource.password")).isEqualTo("secret");
    }
}
