package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.WorkingMemoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingMemoryJpaRepository extends JpaRepository<WorkingMemoryEntity, String> {}
