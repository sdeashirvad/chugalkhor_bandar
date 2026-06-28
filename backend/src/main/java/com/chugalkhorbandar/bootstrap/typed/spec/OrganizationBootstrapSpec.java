package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record OrganizationBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String organizationId,
        String name,
        String type,
        String leader,
        String headquarters,
        String purpose,
        String knownMembers,
        String rules,
        String notes,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
