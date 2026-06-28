package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.WorldTickHistoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorldTickHistoryJpaRepository extends JpaRepository<WorldTickHistoryEntity, String> {

    List<WorldTickHistoryEntity> findAllByOrderByStartedAtDesc();

    Optional<WorldTickHistoryEntity> findFirstByModeOrderByStartedAtDesc(String mode);
}
