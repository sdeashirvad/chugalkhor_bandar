package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record CanonBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String canonicalFacts,
        String worldTruths,
        String stableRules,
        String importantLoreReferences,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
