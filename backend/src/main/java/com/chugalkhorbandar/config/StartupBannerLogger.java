package com.chugalkhorbandar.config;

import com.chugalkhorbandar.application.llm.LLMProviderRegistry;
import com.chugalkhorbandar.ports.PersistenceProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Order(8)
public class StartupBannerLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupBannerLogger.class);

    private final ChugalkhorProperties properties;
    private final Environment environment;
    private final PersistenceProvider persistenceProvider;
    private final LlmProperties llmProperties;
    private final LLMProviderRegistry llmProviderRegistry;

    public StartupBannerLogger(
            ChugalkhorProperties properties,
            Environment environment,
            PersistenceProvider persistenceProvider,
            LlmProperties llmProperties,
            LLMProviderRegistry llmProviderRegistry) {
        this.properties = properties;
        this.environment = environment;
        this.persistenceProvider = persistenceProvider;
        this.llmProperties = llmProperties;
        this.llmProviderRegistry = llmProviderRegistry;
    }

    @Override
    public void run(ApplicationArguments args) {
        String activeProfile = resolveActiveProfile();
        String bootstrapStatus = resolveBootstrapStatus();
        String chronicleStatus = resolveChronicleStatus();

        log.info("===================================");
        log.info("");
        log.info("{}", properties.getName());
        log.info("");
        log.info("Profile : {}", activeProfile);
        log.info("");
        log.info("Persistence : {}", persistenceProvider.getType().getDisplayName());
        log.info("");
        log.info("Bootstrap : {}", bootstrapStatus);
        log.info("");
        log.info("Chronicles : {}", chronicleStatus);
        log.info("");
        log.info("LLM Provider : {} ({})", llmProperties.getProvider(), llmProviderRegistry.activeProvider().providerType());
        log.info("LLM Model : {}", llmProperties.getModel());
        log.info("");
        log.info("===================================");
    }

    private String resolveActiveProfile() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) {
            return "default";
        }
        return String.join(", ", profiles);
    }

    private String resolveBootstrapStatus() {
        Path bootstrapPath = Path.of(properties.getBootstrapFolder()).toAbsolutePath().normalize();
        return Files.isDirectory(bootstrapPath) ? "FOUND" : "MISSING";
    }

    private String resolveChronicleStatus() {
        Path chroniclePath = Path.of(properties.getChronicleFolder()).toAbsolutePath().normalize();
        return Files.isDirectory(chroniclePath) ? "READY" : "MISSING";
    }
}
