package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CanonJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CharacterJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CustomJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.GlossaryEntryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.LawJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ObjectJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.OrganizationJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.PlaceJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.PromptProfileJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.RelationshipJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ResourceJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.StoryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.TerritoryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.TimelineEntryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.WorldRulesJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CharacterCredentialJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ConversationJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ConversationMessageJpaRepository;
import com.chugalkhorbandar.domain.conversation.ports.ConversationMessageRepository;
import com.chugalkhorbandar.domain.conversation.ports.ConversationRepository;
import com.chugalkhorbandar.domain.identity.ports.CharacterCredentialRepository;
import com.chugalkhorbandar.domain.world.ports.WorldPersistenceService;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Profile("postgres-dev")
@EnableJpaRepositories(basePackages = "com.chugalkhorbandar.adapters.persistence.postgres.jpa")
@EntityScan(basePackages = "com.chugalkhorbandar.adapters.persistence.postgres.entity")
public class PostgresPersistenceConfiguration {

    @Bean
    @org.springframework.context.annotation.Primary
    WorldRepositoryProvider postgresWorldRepositoryProvider(
            CharacterJpaRepository characterJpa,
            TerritoryJpaRepository territoryJpa,
            PlaceJpaRepository placeJpa,
            StoryJpaRepository storyJpa,
            RelationshipJpaRepository relationshipJpa,
            OrganizationJpaRepository organizationJpa,
            ResourceJpaRepository resourceJpa,
            ObjectJpaRepository objectJpa,
            TimelineEntryJpaRepository timelineJpa,
            PromptProfileJpaRepository promptProfileJpa,
            CanonJpaRepository canonJpa,
            WorldRulesJpaRepository worldRulesJpa,
            LawJpaRepository lawJpa,
            CustomJpaRepository customJpa,
            GlossaryEntryJpaRepository glossaryJpa) {
        return new PostgresWorldRepositoryProvider(
                characterJpa,
                territoryJpa,
                placeJpa,
                storyJpa,
                relationshipJpa,
                organizationJpa,
                resourceJpa,
                objectJpa,
                timelineJpa,
                promptProfileJpa,
                canonJpa,
                worldRulesJpa,
                lawJpa,
                customJpa,
                glossaryJpa);
    }

    @Bean
    @org.springframework.context.annotation.Primary
    WorldPersistenceService postgresWorldPersistenceService(
            WorldRepositoryProvider provider, PlatformTransactionManager transactionManager) {
        return new PostgresWorldPersistenceService(provider, transactionManager);
    }

    @Bean
    CharacterCredentialRepository characterCredentialRepository(CharacterCredentialJpaRepository jpa) {
        return new PostgresCharacterCredentialRepository(jpa);
    }

    @Bean
    ConversationRepository conversationRepository(
            ConversationJpaRepository conversationJpa, ConversationMessageJpaRepository messageJpa) {
        return new PostgresConversationRepository(conversationJpa, messageJpa);
    }

    @Bean
    ConversationMessageRepository conversationMessageRepository(ConversationMessageJpaRepository messageJpa) {
        return new PostgresConversationMessageRepository(messageJpa);
    }
}
