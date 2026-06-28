package com.chugalkhorbandar.application.memory.consolidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MemoryConsolidationAsyncRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryConsolidationAsyncRunner.class);

    private final MemoryConsolidationService consolidationService;

    public MemoryConsolidationAsyncRunner(MemoryConsolidationService consolidationService) {
        this.consolidationService = consolidationService;
    }

    @Async("memoryConsolidationExecutor")
    public void runAsync() {
        try {
            consolidationService.runConsolidation();
        } catch (Exception exception) {
            LOGGER.warn("Async memory consolidation failed", exception);
        }
    }
}
