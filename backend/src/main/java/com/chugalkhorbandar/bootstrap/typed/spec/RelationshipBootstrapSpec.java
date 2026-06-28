package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record RelationshipBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String relationshipType,
        String characters,
        String description,
        String relationshipStatus,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
