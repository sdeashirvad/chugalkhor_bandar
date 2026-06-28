package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.CharacterCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterCredentialJpaRepository extends JpaRepository<CharacterCredentialEntity, String> {}
