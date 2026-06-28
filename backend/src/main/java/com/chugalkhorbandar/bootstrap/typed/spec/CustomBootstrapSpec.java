package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record CustomBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String category,
        String customTitle,
        String description,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
