package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.LongTermMemoryCandidateEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LongTermMemoryCandidateJpaRepository extends JpaRepository<LongTermMemoryCandidateEntity, String> {

    List<LongTermMemoryCandidateEntity> findByRunIdOrderByCreatedAtDesc(String runId);

    List<LongTermMemoryCandidateEntity> findAllByOrderByCreatedAtDesc();
}
