package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.ObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectJpaRepository extends JpaRepository<ObjectEntity, String> {}
