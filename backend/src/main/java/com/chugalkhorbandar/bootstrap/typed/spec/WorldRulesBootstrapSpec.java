package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record WorldRulesBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String whatIsCanon,
        String whatCanChange,
        String contradictionRules,
        String secrecyRules,
        String preferencesRules,
        String titleRules,
        String deathRules,
        String storyContinuityRules,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
