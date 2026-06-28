package com.chugalkhorbandar.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

public class DotEnvApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Map<String, String> loaded = DotEnvLoader.load();
        if (loaded.isEmpty()) {
            return;
        }

        Map<String, Object> properties = new LinkedHashMap<>(loaded);
        DotEnvLoader.mapEnvToSpringProperties(properties);
        applicationContext.getEnvironment().getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        ConfigurationPropertySources.attach(applicationContext.getEnvironment());
    }
}
