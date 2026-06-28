package com.chugalkhorbandar.domain.reporting.ports;

import com.chugalkhorbandar.application.reporting.ReportArchive;
import java.util.List;
import java.util.Optional;

public interface ReportArchiveRepository {

    ReportArchive save(ReportArchive archive);

    Optional<ReportArchive> findByReportId(String reportId);

    List<ReportArchive> findAllOrderByCreatedAtDesc();
}
