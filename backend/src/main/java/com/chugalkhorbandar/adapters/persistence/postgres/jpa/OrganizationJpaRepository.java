package com.chugalkhorbandar.adapters.persistence.postgres.jpa;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, String> {}
