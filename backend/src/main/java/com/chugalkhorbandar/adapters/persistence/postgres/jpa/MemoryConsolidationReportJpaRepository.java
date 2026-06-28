package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.MemoryConsolidationReportEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryConsolidationReportJpaRepository extends JpaRepository<MemoryConsolidationReportEntity, String> {

    Optional<MemoryConsolidationReportEntity> findFirstByOrderByStartedAtDesc();

    List<MemoryConsolidationReportEntity> findAllByOrderByStartedAtDesc();
}
