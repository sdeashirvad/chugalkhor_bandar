package com.chugalkhorbandar.domain.reporting.ports;

import com.chugalkhorbandar.application.reporting.DeliveryHistory;
import java.util.List;
import java.util.Optional;

public interface DeliveryHistoryRepository {

    DeliveryHistory save(DeliveryHistory entry);

    Optional<DeliveryHistory> findById(String id);

    List<DeliveryHistory> findByReportIdOrderByCreatedAtDesc(String reportId);

    List<DeliveryHistory> findAllOrderByCreatedAtDesc();

    List<DeliveryHistory> findFailedEligibleForRetry(int maxAttempts);
}
