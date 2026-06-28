package com.chugalkhorbandar.config;

import com.chugalkhorbandar.application.chronicle.ChronicleWriterGenerationStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChronicleWriterConfiguration {

    @Bean
    ChronicleWriterGenerationStore chronicleWriterGenerationStore() {
        return new ChronicleWriterGenerationStore();
    }
}
