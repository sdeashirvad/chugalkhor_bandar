package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.TerritoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TerritoryJpaRepository extends JpaRepository<TerritoryEntity, String> {}
