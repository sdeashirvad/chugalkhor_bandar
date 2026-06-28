package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.domain.identity.ports.CharacterCredentialRepository;
import com.chugalkhorbandar.domain.conversation.ports.ConversationMessageRepository;
import com.chugalkhorbandar.domain.conversation.ports.ConversationRepository;
import com.chugalkhorbandar.domain.world.ports.WorldPersistenceService;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev & !postgres-dev")
public class InMemoryPersistenceConfiguration {

    @Bean
    InMemoryWorldStore inMemoryWorldStore() {
        return new InMemoryWorldStore();
    }

    @Bean
    WorldRepositoryProvider worldRepositoryProvider(InMemoryWorldStore store) {
        return new InMemoryWorldRepositoryProvider(store);
    }

    @Bean
    WorldPersistenceService worldPersistenceService(
            InMemoryWorldStore store, WorldRepositoryProvider worldRepositoryProvider) {
        return new InMemoryWorldPersistenceService(store, worldRepositoryProvider);
    }

    @Bean
    CharacterCredentialRepository characterCredentialRepository() {
        return new InMemoryCharacterCredentialRepository();
    }

    @Bean
    InMemoryConversationStore inMemoryConversationStore() {
        return new InMemoryConversationStore();
    }

    @Bean
    ConversationRepository conversationRepository(InMemoryConversationStore store) {
        return new InMemoryConversationRepository(store);
    }

    @Bean
    ConversationMessageRepository conversationMessageRepository(InMemoryConversationStore store) {
        return new InMemoryConversationMessageRepository(store);
    }

    @Bean
    InMemoryWorkingMemoryStore inMemoryWorkingMemoryStore() {
        return new InMemoryWorkingMemoryStore();
    }

    @Bean
    InMemoryNotificationStore inMemoryNotificationStore() {
        return new InMemoryNotificationStore();
    }

    @Bean
    InMemoryConversationArtifactStore inMemoryConversationArtifactStore() {
        return new InMemoryConversationArtifactStore();
    }

    @Bean
    InMemoryCognitiveAnalysisStore inMemoryCognitiveAnalysisStore() {
        return new InMemoryCognitiveAnalysisStore();
    }

    @Bean
    InMemoryMemoryInboxStore inMemoryMemoryInboxStore() {
        return new InMemoryMemoryInboxStore();
    }

    @Bean
    InMemoryMemoryConsolidationStore inMemoryMemoryConsolidationStore() {
        return new InMemoryMemoryConsolidationStore();
    }

    @Bean
    InMemoryReportingStore inMemoryReportingStore() {
        return new InMemoryReportingStore();
    }

    @Bean
    InMemoryChronicleStore inMemoryChronicleStore() {
        return new InMemoryChronicleStore();
    }

    @Bean
    InMemoryLivingWorldStore inMemoryLivingWorldStore() {
        return new InMemoryLivingWorldStore();
    }
}
