package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.CanonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanonJpaRepository extends JpaRepository<CanonEntity, String> {}
