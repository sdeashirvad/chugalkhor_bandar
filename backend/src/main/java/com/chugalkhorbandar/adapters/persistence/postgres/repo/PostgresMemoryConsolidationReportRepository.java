package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.MemoryConsolidationReportEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.MemoryConsolidationReportJpaRepository;
import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.chugalkhorbandar.domain.memory.consolidation.ports.MemoryConsolidationReportRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresMemoryConsolidationReportRepository implements MemoryConsolidationReportRepository {

    private final MemoryConsolidationReportJpaRepository jpaRepository;

    public PostgresMemoryConsolidationReportRepository(MemoryConsolidationReportJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MemoryConsolidationReport save(MemoryConsolidationReport report) {
        jpaRepository.save(toEntity(report));
        return report;
    }

    @Override
    public Optional<MemoryConsolidationReport> findByRunId(String runId) {
        return jpaRepository.findById(runId).map(this::toDomain);
    }

    @Override
    public Optional<MemoryConsolidationReport> findLatest() {
        return jpaRepository.findFirstByOrderByStartedAtDesc().map(this::toDomain);
    }

    @Override
    public List<MemoryConsolidationReport> findHistoryOrderByStartedAtDesc() {
        return jpaRepository.findAllByOrderByStartedAtDesc().stream().map(this::toDomain).toList();
    }

    private MemoryConsolidationReport toDomain(MemoryConsolidationReportEntity entity) {
        return new MemoryConsolidationReport(
                entity.getRunId(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getDurationMs(),
                entity.getProcessed(),
                entity.getPromoted(),
                entity.getDiscarded(),
                entity.getExpired(),
                entity.getArchived(),
                entity.getPending(),
                entity.getCandidateCount(),
                entity.getSummary(),
                entity.getTxtReport(),
                entity.getJsonReport(),
                entity.getReflection(),
                entity.getEmailStatus(),
                entity.getEmailError());
    }

    private MemoryConsolidationReportEntity toEntity(MemoryConsolidationReport report) {
        return new MemoryConsolidationReportEntity(
                report.runId(),
                report.startedAt(),
                report.completedAt(),
                report.durationMs(),
                report.processed(),
                report.promoted(),
                report.discarded(),
                report.expired(),
                report.archived(),
                report.pending(),
                report.candidateCount(),
                report.summary(),
                report.txtReport(),
                report.jsonReport(),
                report.reflection(),
                report.emailStatus(),
                report.emailError());
    }
}
