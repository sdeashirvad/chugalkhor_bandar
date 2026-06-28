package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.reporting.ReportArchive;
import com.chugalkhorbandar.domain.reporting.ports.ReportArchiveRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryReportArchiveRepository implements ReportArchiveRepository {

    private final InMemoryReportingStore store;

    public InMemoryReportArchiveRepository(InMemoryReportingStore store) {
        this.store = store;
    }

    @Override
    public ReportArchive save(ReportArchive archive) {
        return store.saveArchive(archive);
    }

    @Override
    public Optional<ReportArchive> findByReportId(String reportId) {
        return store.findArchiveByReportId(reportId);
    }

    @Override
    public List<ReportArchive> findAllOrderByCreatedAtDesc() {
        return store.findAllArchives();
    }
}
