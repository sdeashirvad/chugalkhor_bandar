package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.TimelineEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineEntryJpaRepository extends JpaRepository<TimelineEntryEntity, String> {}
