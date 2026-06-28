package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.CognitiveAnalysisDiagnosticEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CognitiveAnalysisDiagnosticJpaRepository
        extends JpaRepository<CognitiveAnalysisDiagnosticEntity, String> {}
