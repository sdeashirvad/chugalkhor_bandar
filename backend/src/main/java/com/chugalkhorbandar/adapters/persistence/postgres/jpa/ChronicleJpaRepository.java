package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ChronicleEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChronicleJpaRepository extends JpaRepository<ChronicleEntity, String> {

    List<ChronicleEntity> findAllByOrderByCreatedAtDesc();

    List<ChronicleEntity> findByCategoryOrderByCreatedAtDesc(String category);

    List<ChronicleEntity> findByVisibilityOrderByCreatedAtDesc(String visibility);

    @Query("SELECT COUNT(c) FROM ChronicleEntity c WHERE c.provenanceJson LIKE CONCAT('%\"candidateId\":\"', :candidateId, '\"%')")
    long countByCandidateId(@Param("candidateId") String candidateId);
}
