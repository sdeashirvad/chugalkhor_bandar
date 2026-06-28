package com.chugalkhorbandar.bootstrap.typed;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentSection;
import com.chugalkhorbandar.bootstrap.typed.spec.BootstrapTypedSpec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SectionReaderSupport {

    private SectionReaderSupport() {}

    public static String requireNonEmptySection(BootstrapDocument document, String sectionTitle) {
        String content = document.getContent(sectionTitle);
        if (content == null || content.isBlank()) {
            throw new TypedReaderException(
                    "Required section is missing or empty: " + sectionTitle,
                    document.metadata().id(),
                    document.sourcePath());
        }
        return content.strip();
    }

    public static String optionalSection(BootstrapDocument document, String sectionTitle) {
        String content = document.getContent(sectionTitle);
        if (content == null || content.isBlank()) {
            return null;
        }
        return content.strip();
    }

    public static Map<String, String> unmappedSections(BootstrapDocument document, Set<String> knownSections) {
        Set<String> known = new HashSet<>();
        for (String section : knownSections) {
            known.add(section.toLowerCase());
        }

        Map<String, String> unmapped = new HashMap<>();
        for (DocumentSection section : document.getSections()) {
            if (!known.contains(section.title().toLowerCase())) {
                unmapped.put(section.title(), section.content());
            }
        }
        return Map.copyOf(unmapped);
    }

    public static Set<String> knownSectionNames(String... names) {
        return Set.of(names);
    }

    public static void requireAtLeastOneSection(BootstrapDocument document) {
        if (document.getSections().isEmpty()) {
            throw new TypedReaderException(
                    "Document must contain at least one section",
                    document.metadata().id(),
                    document.sourcePath());
        }
    }
}
