package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterJpaRepository extends JpaRepository<CharacterEntity, String> {}
