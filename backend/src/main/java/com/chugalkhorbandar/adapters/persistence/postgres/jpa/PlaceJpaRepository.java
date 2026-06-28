package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceJpaRepository extends JpaRepository<PlaceEntity, String> {}
