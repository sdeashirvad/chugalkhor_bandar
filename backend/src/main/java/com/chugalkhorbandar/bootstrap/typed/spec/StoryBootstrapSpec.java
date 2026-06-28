package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record StoryBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String summary,
        String participants,
        String majorPlaces,
        String beginning,
        String keyEvents,
        String ending,
        String canonicalConsequences,
        String linkedCharacters,
        String linkedPlaces,
        String linkedOrganizations,
        String linkedStories,
        String notes,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
