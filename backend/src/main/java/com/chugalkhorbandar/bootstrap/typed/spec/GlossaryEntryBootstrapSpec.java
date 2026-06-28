package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record GlossaryEntryBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String term,
        String definition,
        String references,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
