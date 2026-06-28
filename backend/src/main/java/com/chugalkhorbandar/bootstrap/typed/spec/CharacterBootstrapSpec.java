package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record CharacterBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String summary,
        String basicInformation,
        String titles,
        String roles,
        String personality,
        String history,
        String dailyRoutine,
        String relationships,
        String knownPreferences,
        String abilities,
        String responsibilities,
        String assets,
        String publicReputation,
        String secrets,
        String notes,
        String currentPlace,
        String homeTerritory,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
