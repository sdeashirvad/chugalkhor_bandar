package com.chugalkhorbandar.application.query;

import com.chugalkhorbandar.domain.world.ports.OrganizationRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.runtime.RuntimeOrganization;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrganizationQueryService {

    private final OrganizationRepository organizations;
    private final EntityReferenceResolver referenceResolver;

    public OrganizationQueryService(
            WorldRepositoryProvider repositoryProvider, EntityReferenceResolver referenceResolver) {
        this.organizations = repositoryProvider.organizations();
        this.referenceResolver = referenceResolver;
    }

    public List<RuntimeOrganization> findAll() {
        return organizations.findAll().stream()
                .sorted(Comparator.comparing(RuntimeOrganization::title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public RuntimeOrganization findById(String id) {
        return organizations.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization", id));
    }

    public OrganizationDetailsView findDetailsById(String id) {
        RuntimeOrganization organization = findById(id);
        return new OrganizationDetailsView(
                organization,
                referenceResolver.resolveCharacter(organization.sections().get("leader")),
                referenceResolver.resolvePlace(organization.sections().get("headquarters")),
                referenceResolver.resolveListItems(
                        organization.sections().get("knownMembers"), EntityReferenceResolver.ReferenceType.CHARACTER));
    }

    public record OrganizationDetailsView(
            RuntimeOrganization organization,
            java.util.Optional<EntityReferenceResolver.ResolvedReference> leader,
            java.util.Optional<EntityReferenceResolver.ResolvedReference> headquarters,
            List<EntityReferenceResolver.ResolvedReference> members) {}
}
