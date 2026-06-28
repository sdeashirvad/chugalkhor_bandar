package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record TerritoryBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String territoryId,
        String name,
        String capital,
        String currentRuler,
        String government,
        String knownJungleCount,
        String history,
        String goals,
        String notes,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
