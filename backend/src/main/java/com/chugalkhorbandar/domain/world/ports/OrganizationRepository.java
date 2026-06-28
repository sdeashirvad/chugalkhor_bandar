package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {

    void create(RuntimeOrganization organization);

    boolean exists(String organizationId);

    Optional<RuntimeOrganization> findById(String organizationId);

    List<RuntimeOrganization> findAll();

    void assignRole(String organizationId, String characterId, String role);
}
