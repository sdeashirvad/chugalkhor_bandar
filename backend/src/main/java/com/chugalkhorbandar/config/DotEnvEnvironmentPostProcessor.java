package com.chugalkhorbandar.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public final class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";

    @Override
    public int getOrder() {
        // Load .env before application.yml so ${POSTGRES_*} placeholders resolve.
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, String> loaded = DotEnvLoader.load();
        if (loaded.isEmpty()) {
            return;
        }

        Map<String, Object> properties = new LinkedHashMap<>(loaded);
        DotEnvLoader.mapEnvToSpringProperties(properties);
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        ConfigurationPropertySources.attach(environment);
    }
}
