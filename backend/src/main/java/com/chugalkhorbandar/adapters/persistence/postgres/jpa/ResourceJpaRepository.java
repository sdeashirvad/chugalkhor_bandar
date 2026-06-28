package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceJpaRepository extends JpaRepository<ResourceEntity, String> {}
