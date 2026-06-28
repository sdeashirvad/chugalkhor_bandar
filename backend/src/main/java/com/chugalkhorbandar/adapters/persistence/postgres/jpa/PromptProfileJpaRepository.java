package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.PromptProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptProfileJpaRepository extends JpaRepository<PromptProfileEntity, String> {}
