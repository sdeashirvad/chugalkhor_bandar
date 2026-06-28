package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.RelationshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationshipJpaRepository extends JpaRepository<RelationshipEntity, String> {}
