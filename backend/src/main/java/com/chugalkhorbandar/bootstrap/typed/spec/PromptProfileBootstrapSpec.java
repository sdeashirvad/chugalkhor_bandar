package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;
import java.util.Map;

public record PromptProfileBootstrapSpec(
        String id,
        String title,
        Path sourcePath,
        String status,
        String version,
        DocumentType documentType,
        String identity,
        String corePersonality,
        String speakingStyle,
        String behavior,
        String forbiddenBehaviors,
        String narrationRules,
        Map<String, String> unmappedSections)
        implements BootstrapTypedSpec {}
