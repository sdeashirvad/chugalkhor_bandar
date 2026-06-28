package com.chugalkhorbandar.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DotEnvLoaderIntegrationTest {

    @Test
    void loadsBackendEnvFileWhenPresent() {
        Map<String, String> loaded = DotEnvLoader.load();
        assertThat(DotEnvLoader.resolveEnvFile()).exists();
        assertThat(loaded).containsKey("POSTGRES_HOST");
        assertThat(loaded.get("POSTGRES_HOST")).isNotBlank().doesNotContain("${");

        Map<String, Object> properties = new LinkedHashMap<>(loaded);
        DotEnvLoader.mapEnvToSpringProperties(properties);

        assertThat(properties.get("spring.datasource.url"))
                .asString()
                .startsWith("jdbc:postgresql://")
                .doesNotContain("${");
    }
}
