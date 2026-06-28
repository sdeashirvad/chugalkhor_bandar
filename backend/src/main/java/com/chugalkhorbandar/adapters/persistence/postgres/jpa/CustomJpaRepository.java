package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.CustomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomJpaRepository extends JpaRepository<CustomEntity, String> {}
