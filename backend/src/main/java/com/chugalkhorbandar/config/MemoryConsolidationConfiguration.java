package com.chugalkhorbandar.config;

import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationAsyncRunner;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationGenerationStore;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationProperties;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationScheduler;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationService;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
@EnableAsync
public class MemoryConsolidationConfiguration {

    @Bean
    MemoryConsolidationGenerationStore memoryConsolidationGenerationStore() {
        return new MemoryConsolidationGenerationStore();
    }

    @Bean(name = "memoryConsolidationExecutor")
    Executor memoryConsolidationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("memory-consolidation-");
        executor.initialize();
        return executor;
    }

    @Bean
    MemoryConsolidationScheduler memoryConsolidationScheduler(
            MemoryConsolidationProperties properties, MemoryConsolidationAsyncRunner asyncRunner) {
        return new MemoryConsolidationScheduler(properties, asyncRunner);
    }
}
