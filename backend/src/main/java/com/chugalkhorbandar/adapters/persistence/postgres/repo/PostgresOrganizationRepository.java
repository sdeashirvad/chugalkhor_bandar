package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.requireRuntime;

import com.chugalkhorbandar.adapters.persistence.postgres.jpa.OrganizationJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.OrganizationMapper;
import com.chugalkhorbandar.domain.world.ports.OrganizationRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import java.util.List;
import java.util.Optional;

public final class PostgresOrganizationRepository implements OrganizationRepository {

    private final OrganizationJpaRepository jpa;

    public PostgresOrganizationRepository(OrganizationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeOrganization organization) {
        ensureAbsent(jpa, organization.id(), "Organizations");
        jpa.save(OrganizationMapper.toEntity(organization));
    }

    @Override
    public boolean exists(String organizationId) {
        return jpa.existsById(organizationId);
    }

    @Override
    public Optional<RuntimeOrganization> findById(String organizationId) {
        return jpa.findById(organizationId).map(OrganizationMapper::toRuntime);
    }

    @Override
    public List<RuntimeOrganization> findAll() {
        return jpa.findAll().stream().map(OrganizationMapper::toRuntime).toList();
    }

    @Override
    public void assignRole(String organizationId, String characterId, String role) {
        RuntimeOrganization organization =
                requireRuntime(findById(organizationId), organizationId, "Organizations");
        jpa.save(OrganizationMapper.toEntity(organization.withRole(characterId, role)));
    }
}
