package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.DeliveryHistoryEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.DeliveryHistoryJpaRepository;
import com.chugalkhorbandar.application.reporting.DeliveryHistory;
import com.chugalkhorbandar.domain.reporting.ports.DeliveryHistoryRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresDeliveryHistoryRepository implements DeliveryHistoryRepository {

    private final DeliveryHistoryJpaRepository jpaRepository;

    public PostgresDeliveryHistoryRepository(DeliveryHistoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DeliveryHistory save(DeliveryHistory entry) {
        jpaRepository.save(toEntity(entry));
        return entry;
    }

    @Override
    public Optional<DeliveryHistory> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<DeliveryHistory> findByReportIdOrderByCreatedAtDesc(String reportId) {
        return jpaRepository.findByReportIdOrderByCreatedAtDesc(reportId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<DeliveryHistory> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<DeliveryHistory> findFailedEligibleForRetry(int maxAttempts) {
        List<DeliveryHistoryEntity> failed =
                jpaRepository.findByStatusAndAttemptLessThanOrderByCreatedAtAsc("FAILED", maxAttempts);
        Map<String, DeliveryHistoryEntity> latestByKey = new HashMap<>();
        for (DeliveryHistoryEntity entity : failed) {
            String key = entity.getReportId() + ":" + entity.getRecipient();
            DeliveryHistoryEntity existing = latestByKey.get(key);
            if (existing == null || entity.getAttempt() > existing.getAttempt()) {
                latestByKey.put(key, entity);
            }
        }
        return latestByKey.values().stream().map(this::toDomain).toList();
    }

    private DeliveryHistory toDomain(DeliveryHistoryEntity entity) {
        return new DeliveryHistory(
                entity.getId(),
                entity.getReportId(),
                entity.getRecipient(),
                entity.getStatus(),
                entity.getProvider(),
                entity.getProviderMessageId(),
                entity.getAttempt(),
                entity.getLatencyMs(),
                entity.getError(),
                entity.getCreatedAt());
    }

    private DeliveryHistoryEntity toEntity(DeliveryHistory entry) {
        return new DeliveryHistoryEntity(
                entry.id(),
                entry.reportId(),
                entry.recipient(),
                entry.status(),
                entry.provider(),
                entry.providerMessageId(),
                entry.attempt(),
                entry.latencyMs(),
                entry.error(),
                entry.createdAt());
    }
}
