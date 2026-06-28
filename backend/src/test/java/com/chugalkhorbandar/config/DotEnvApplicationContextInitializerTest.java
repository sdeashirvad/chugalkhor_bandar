package com.chugalkhorbandar.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class DotEnvApplicationContextInitializerTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withInitializer(new DotEnvApplicationContextInitializer());

    @Test
    void addsResolvedDatasourceUrlToEnvironment() {
        contextRunner.run(context -> {
            assertThat(context.getEnvironment().getPropertySources().contains("dotenvProperties"))
                    .isTrue();
            assertThat(context.getEnvironment().getProperty("spring.datasource.url"))
                    .isNotBlank()
                    .doesNotContain("${");
        });
    }
}
