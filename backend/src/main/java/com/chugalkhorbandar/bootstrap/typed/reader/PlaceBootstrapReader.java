package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentSection;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.NestedSectionParser;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.PlaceBootstrapSpec;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PlaceBootstrapReader extends AbstractBootstrapTypedReader<PlaceBootstrapSpec> {

    private static final Set<String> FIELD_NAMES = SectionReaderSupport.knownSectionNames(
            "ID",
            "Type",
            "Description",
            "Current Owner",
            "Located In",
            "Connected Places",
            "Important Locations",
            "Notes",
            "Functions",
            "Regular Visitors",
            "Current Resident",
            "Current Ruler");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.PLACES;
    }

    @Override
    public PlaceBootstrapSpec read(BootstrapDocument document) {
        List<PlaceBootstrapSpec> places = readAll(document);
        if (places.isEmpty()) {
            SectionReaderSupport.requireAtLeastOneSection(document);
            throw new com.chugalkhorbandar.bootstrap.typed.TypedReaderException(
                    "No place entries with place IDs found in document",
                    document.metadata().id(),
                    document.sourcePath());
        }
        return places.getFirst();
    }

    public List<PlaceBootstrapSpec> readAll(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        List<PlaceBootstrapSpec> places = new ArrayList<>();
        List<DocumentSection> sections = document.getSections();
        String sourceDocumentId = id(document);

        for (int index = 0; index < sections.size(); index++) {
            DocumentSection section = sections.get(index);
            if (!"ID".equalsIgnoreCase(section.title())) {
                continue;
            }
            readPlaceAtIdSection(document, sourceDocumentId, sections, index).ifPresent(places::add);
        }

        return List.copyOf(places);
    }

    private Optional<PlaceBootstrapSpec> readPlaceAtIdSection(
            BootstrapDocument document,
            String sourceDocumentId,
            List<DocumentSection> sections,
            int idSectionIndex) {
        DocumentSection idSection = sections.get(idSectionIndex);
        String placeId = NestedSectionParser.extractEntityId(Map.of("ID", idSection.content()), "place_");
        if (placeId == null) {
            return Optional.empty();
        }

        String placeTitle = findPlaceTitle(sections, idSectionIndex);
        Map<String, String> fields = collectPlaceFields(sections, idSectionIndex);
        Map<String, String> unmapped = new LinkedHashMap<>();
        fields.forEach((key, value) -> {
            if (!FIELD_NAMES.contains(key)) {
                unmapped.put(key, value);
            }
        });

        return Optional.of(new PlaceBootstrapSpec(
                placeId,
                placeTitle,
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                sourceDocumentId,
                fields.get("Type"),
                fields.get("Description"),
                fields.get("Current Owner"),
                fields.get("Located In"),
                fields.get("Connected Places"),
                fields.get("Important Locations"),
                fields.get("Notes"),
                Map.copyOf(unmapped)));
    }

    private static String findPlaceTitle(List<DocumentSection> sections, int idSectionIndex) {
        for (int index = idSectionIndex - 1; index >= 0; index--) {
            String title = sections.get(index).title();
            if (!title.isBlank() && !FIELD_NAMES.contains(title)) {
                return title;
            }
        }
        return "Unknown Place";
    }

    private static Map<String, String> collectPlaceFields(List<DocumentSection> sections, int idSectionIndex) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (int index = idSectionIndex; index < sections.size(); index++) {
            DocumentSection section = sections.get(index);
            if (index > idSectionIndex && !FIELD_NAMES.contains(section.title())) {
                break;
            }
            if (FIELD_NAMES.contains(section.title())) {
                fields.put(section.title(), sanitizeFieldContent(section.content()));
            }
        }
        return fields;
    }

    private static String sanitizeFieldContent(String content) {
        if (content == null) {
            return null;
        }
        String cleaned = content.strip();
        if (cleaned.endsWith("---")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).strip();
        }
        return cleaned;
    }
}
