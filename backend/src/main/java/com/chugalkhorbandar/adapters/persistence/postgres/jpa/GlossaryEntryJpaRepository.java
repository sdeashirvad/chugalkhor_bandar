package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.GlossaryEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlossaryEntryJpaRepository extends JpaRepository<GlossaryEntryEntity, String> {}
