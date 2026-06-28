package com.chugalkhorbandar.application.world.living;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class LivingWorldScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LivingWorldScheduler.class);

    private final LivingWorldProperties properties;
    private final LivingWorldService livingWorldService;

    public LivingWorldScheduler(LivingWorldProperties properties, LivingWorldService livingWorldService) {
        this.properties = properties;
        this.livingWorldService = livingWorldService;
    }

    @Scheduled(cron = "${chugalkhor.living-world.schedule:0 0 * * * *}")
    public void runScheduledWorldTick() {
        if (!properties.isEnabled()) {
            return;
        }
        LOGGER.info("Starting scheduled living world tick");
        livingWorldService.runScheduledTicks(java.time.Instant.now());
    }
}
