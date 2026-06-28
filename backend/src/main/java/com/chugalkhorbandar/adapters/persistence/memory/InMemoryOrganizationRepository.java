package com.chugalkhorbandar.adapters.persistence.memory;

import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.putUnique;
import static com.chugalkhorbandar.adapters.persistence.memory.InMemoryRepositorySupport.requirePresent;

import com.chugalkhorbandar.domain.world.ports.OrganizationRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import java.util.List;
import java.util.Optional;

public final class InMemoryOrganizationRepository implements OrganizationRepository {

    private final InMemoryWorldStore store;

    public InMemoryOrganizationRepository(InMemoryWorldStore store) {
        this.store = store;
    }

    @Override
    public void create(RuntimeOrganization organization) {
        putUnique(store.organizations(), organization.id(), organization, "Organizations");
    }

    @Override
    public boolean exists(String organizationId) {
        return store.organizations().containsKey(organizationId);
    }

    @Override
    public Optional<RuntimeOrganization> findById(String organizationId) {
        return Optional.ofNullable(store.organizations().get(organizationId));
    }

    @Override
    public List<RuntimeOrganization> findAll() {
        return List.copyOf(store.organizations().values());
    }

    @Override
    public void assignRole(String organizationId, String characterId, String role) {
        RuntimeOrganization organization =
                requirePresent(store.organizations(), organizationId, "Organizations");
        store.organizations().put(organizationId, organization.withRole(characterId, role));
    }
}
