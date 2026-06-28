package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.StoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryJpaRepository extends JpaRepository<StoryEntity, String> {}
