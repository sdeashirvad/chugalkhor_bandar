package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.DeliveryHistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryHistoryJpaRepository extends JpaRepository<DeliveryHistoryEntity, String> {

    List<DeliveryHistoryEntity> findAllByOrderByCreatedAtDesc();

    List<DeliveryHistoryEntity> findByReportIdOrderByCreatedAtDesc(String reportId);

    List<DeliveryHistoryEntity> findByStatusAndAttemptLessThanOrderByCreatedAtAsc(String status, int attempt);
}
