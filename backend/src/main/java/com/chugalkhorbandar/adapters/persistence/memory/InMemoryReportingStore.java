package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.reporting.DeliveryHistory;
import com.chugalkhorbandar.application.reporting.ReportArchive;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryReportingStore {

    private final ConcurrentHashMap<String, ReportArchive> archivesByReportId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, DeliveryHistory> historyById = new ConcurrentHashMap<>();

    public ReportArchive saveArchive(ReportArchive archive) {
        archivesByReportId.put(archive.reportId(), archive);
        return archive;
    }

    public Optional<ReportArchive> findArchiveByReportId(String reportId) {
        return Optional.ofNullable(archivesByReportId.get(reportId));
    }

    public List<ReportArchive> findAllArchives() {
        return archivesByReportId.values().stream()
                .sorted(Comparator.comparing(ReportArchive::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public DeliveryHistory saveHistory(DeliveryHistory entry) {
        historyById.put(entry.id(), entry);
        return entry;
    }

    public Optional<DeliveryHistory> findHistoryById(String id) {
        return Optional.ofNullable(historyById.get(id));
    }

    public List<DeliveryHistory> findHistoryByReportId(String reportId) {
        return historyById.values().stream()
                .filter(entry -> reportId.equals(entry.reportId()))
                .sorted(Comparator.comparing(DeliveryHistory::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<DeliveryHistory> findAllHistory() {
        return historyById.values().stream()
                .sorted(Comparator.comparing(DeliveryHistory::createdAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<DeliveryHistory> findFailedEligibleForRetry(int maxAttempts) {
        Map<String, DeliveryHistory> latestFailedByKey = new HashMap<>();
        for (DeliveryHistory entry : historyById.values()) {
            if (!"FAILED".equals(entry.status()) || entry.attempt() >= maxAttempts) {
                continue;
            }
            String key = entry.reportId() + ":" + entry.recipient();
            DeliveryHistory existing = latestFailedByKey.get(key);
            if (existing == null || entry.attempt() > existing.attempt()) {
                latestFailedByKey.put(key, entry);
            }
        }
        return latestFailedByKey.values().stream()
                .sorted(Comparator.comparing(DeliveryHistory::createdAt))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
