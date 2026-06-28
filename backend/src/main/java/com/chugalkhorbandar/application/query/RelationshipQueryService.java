package com.chugalkhorbandar.application.query;

import com.chugalkhorbandar.domain.world.ports.RelationshipRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.RelationshipQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeRelationship;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RelationshipQueryService {

    private final RelationshipRepository relationships;
    private final EntityReferenceResolver referenceResolver;

    public RelationshipQueryService(
            WorldRepositoryProvider repositoryProvider, EntityReferenceResolver referenceResolver) {
        this.relationships = repositoryProvider.relationships();
        this.referenceResolver = referenceResolver;
    }

    public List<RuntimeRelationship> findAll() {
        return relationships.findAll(RelationshipQuery.all()).stream()
                .sorted(Comparator.comparing(RuntimeRelationship::title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public RuntimeRelationship findById(String id) {
        return relationships.findById(id).orElseThrow(() -> new ResourceNotFoundException("Relationship", id));
    }

    public RelationshipDetailsView findDetailsById(String id) {
        RuntimeRelationship relationship = findById(id);
        return new RelationshipDetailsView(
                relationship,
                referenceResolver.resolveListItems(
                        relationship.sections().get("characters"), EntityReferenceResolver.ReferenceType.CHARACTER));
    }

    public record RelationshipDetailsView(
            RuntimeRelationship relationship, List<EntityReferenceResolver.ResolvedReference> characters) {}
}
