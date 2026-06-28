package com.chugalkhorbandar.config;

import com.chugalkhorbandar.application.world.living.LivingWorldGenerationStore;
import com.chugalkhorbandar.application.world.living.LivingWorldProperties;
import com.chugalkhorbandar.application.world.living.LivingWorldScheduler;
import com.chugalkhorbandar.application.world.living.LivingWorldService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LivingWorldProperties.class)
public class LivingWorldConfiguration {

    @Bean
    LivingWorldGenerationStore livingWorldGenerationStore() {
        return new LivingWorldGenerationStore();
    }

    @Bean
    LivingWorldScheduler livingWorldScheduler(LivingWorldProperties properties, LivingWorldService service) {
        return new LivingWorldScheduler(properties, service);
    }
}
