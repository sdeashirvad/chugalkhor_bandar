package com.chugalkhorbandar.application.memory.consolidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class MemoryConsolidationScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryConsolidationScheduler.class);

    private final MemoryConsolidationProperties properties;
    private final MemoryConsolidationAsyncRunner asyncRunner;

    public MemoryConsolidationScheduler(
            MemoryConsolidationProperties properties, MemoryConsolidationAsyncRunner asyncRunner) {
        this.properties = properties;
        this.asyncRunner = asyncRunner;
    }

    @Scheduled(cron = "${chugalkhor.memory-consolidation.schedule:0 0 0 * * *}")
    public void runScheduledConsolidation() {
        if (!properties.isEnabled()) {
            return;
        }
        LOGGER.info("Starting scheduled memory consolidation");
        asyncRunner.runAsync();
    }
}
