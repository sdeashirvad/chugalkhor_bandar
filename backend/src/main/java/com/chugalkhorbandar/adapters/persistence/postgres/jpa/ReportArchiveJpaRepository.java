package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ReportArchiveEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportArchiveJpaRepository extends JpaRepository<ReportArchiveEntity, String> {

    List<ReportArchiveEntity> findAllByOrderByCreatedAtDesc();
}
