package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record ObjectBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String objectId,
        String name,
        String type,
        String owner,
        String location,
        String description,
        String history,
        String rules,
        String notes,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
