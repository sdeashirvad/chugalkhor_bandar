package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.WorldRulesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorldRulesJpaRepository extends JpaRepository<WorldRulesEntity, String> {}
