package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ReportArchiveEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.ReportArchiveJpaRepository;
import com.chugalkhorbandar.application.reporting.ReportArchive;
import com.chugalkhorbandar.domain.reporting.ports.ReportArchiveRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("postgres-dev")
public class PostgresReportArchiveRepository implements ReportArchiveRepository {

    private final ReportArchiveJpaRepository jpaRepository;

    public PostgresReportArchiveRepository(ReportArchiveJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ReportArchive save(ReportArchive archive) {
        jpaRepository.save(toEntity(archive));
        return archive;
    }

    @Override
    public Optional<ReportArchive> findByReportId(String reportId) {
        return jpaRepository.findById(reportId).map(this::toDomain);
    }

    @Override
    public List<ReportArchive> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    private ReportArchive toDomain(ReportArchiveEntity entity) {
        return new ReportArchive(
                entity.getReportId(),
                entity.getHtmlContent(),
                entity.getTxtContent(),
                entity.getJsonContent(),
                entity.getMarkdownContent(),
                entity.getCreatedAt());
    }

    private ReportArchiveEntity toEntity(ReportArchive archive) {
        return new ReportArchiveEntity(
                archive.reportId(),
                archive.htmlContent(),
                archive.txtContent(),
                archive.jsonContent(),
                archive.markdownContent(),
                archive.createdAt());
    }
}
