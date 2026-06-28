package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record ChronologyBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        List<ChronologyTimelineItem> timelineItems,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
