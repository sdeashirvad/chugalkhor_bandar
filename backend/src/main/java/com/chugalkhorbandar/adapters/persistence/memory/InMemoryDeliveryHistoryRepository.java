package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.reporting.DeliveryHistory;
import com.chugalkhorbandar.domain.reporting.ports.DeliveryHistoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryDeliveryHistoryRepository implements DeliveryHistoryRepository {

    private final InMemoryReportingStore store;

    public InMemoryDeliveryHistoryRepository(InMemoryReportingStore store) {
        this.store = store;
    }

    @Override
    public DeliveryHistory save(DeliveryHistory entry) {
        return store.saveHistory(entry);
    }

    @Override
    public Optional<DeliveryHistory> findById(String id) {
        return store.findHistoryById(id);
    }

    @Override
    public List<DeliveryHistory> findByReportIdOrderByCreatedAtDesc(String reportId) {
        return store.findHistoryByReportId(reportId);
    }

    @Override
    public List<DeliveryHistory> findAllOrderByCreatedAtDesc() {
        return store.findAllHistory();
    }

    @Override
    public List<DeliveryHistory> findFailedEligibleForRetry(int maxAttempts) {
        return store.findFailedEligibleForRetry(maxAttempts);
    }
}
