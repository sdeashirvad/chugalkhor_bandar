package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.NotificationEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.NotificationJpaRepository;
import com.chugalkhorbandar.application.notification.Notification;
import com.chugalkhorbandar.application.notification.NotificationPriority;
import com.chugalkhorbandar.application.notification.NotificationStatus;
import com.chugalkhorbandar.application.notification.NotificationType;
import com.chugalkhorbandar.domain.notification.ports.NotificationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresNotificationRepository implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public PostgresNotificationRepository(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Notification> findByRecipientCharacterId(String recipientCharacterId) {
        return jpaRepository.findByRecipientCharacterIdOrderByCreatedAtDesc(recipientCharacterId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Notification> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Notification save(Notification notification) {
        jpaRepository.save(toEntity(notification));
        return notification;
    }

    @Override
    public long countUnreadByRecipientCharacterId(String recipientCharacterId) {
        return jpaRepository.countByRecipientCharacterIdAndStatus(
                recipientCharacterId, NotificationStatus.DELIVERED.name());
    }

    private Notification toDomain(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getRecipientCharacterId(),
                NotificationType.valueOf(entity.getType()),
                NotificationPriority.valueOf(entity.getPriority()),
                entity.getTitle(),
                entity.getSummary(),
                NotificationStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getSource(),
                entity.getTriggerName(),
                readMetadata(entity.getMetadataJson()));
    }

    private NotificationEntity toEntity(Notification notification) {
        return new NotificationEntity(
                notification.id(),
                notification.recipientCharacterId(),
                notification.type().name(),
                notification.priority().name(),
                notification.title(),
                notification.summary(),
                notification.status().name(),
                notification.createdAt(),
                notification.expiresAt(),
                notification.source(),
                notification.trigger(),
                writeMetadata(notification.metadata()));
    }

    private Map<String, String> readMetadata(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception exception) {
            return Map.of();
        }
    }

    private String writeMetadata(Map<String, String> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize notification metadata", exception);
        }
    }
}
