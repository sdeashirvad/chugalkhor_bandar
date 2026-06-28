package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.WorldEventEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorldEventJpaRepository extends JpaRepository<WorldEventEntity, String> {

    List<WorldEventEntity> findAllByOrderByCreatedAtDesc();

    List<WorldEventEntity> findByTypeOrderByCreatedAtDesc(String type);
}
