package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.BootstrapTypedSpec;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

abstract class AbstractBootstrapTypedReader<T extends BootstrapTypedSpec> implements BootstrapTypedReader<T> {

    protected String optional(BootstrapDocument document, String section) {
        return SectionReaderSupport.optionalSection(document, section);
    }

    protected String require(BootstrapDocument document, String section) {
        return SectionReaderSupport.requireNonEmptySection(document, section);
    }

    protected Map<String, String> unmapped(BootstrapDocument document, Set<String> known) {
        return SectionReaderSupport.unmappedSections(document, known);
    }

    protected String id(BootstrapDocument document) {
        return document.metadata().id();
    }

    protected String title(BootstrapDocument document) {
        return document.metadata().title();
    }

    protected Path sourcePath(BootstrapDocument document) {
        return document.sourcePath();
    }

    protected String status(BootstrapDocument document) {
        return document.metadata().status();
    }

    protected String version(BootstrapDocument document) {
        return document.metadata().version();
    }

    protected DocumentType documentType(BootstrapDocument document) {
        return document.documentType();
    }

    protected String frontmatterString(BootstrapDocument document, String key) {
        Object value = document.frontmatter().get(key);
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    protected String placeIdFromFrontmatter(BootstrapDocument document, String key) {
        return entityIdFromFrontmatter(document, key, "place_");
    }

    protected String territoryIdFromFrontmatter(BootstrapDocument document, String key) {
        return entityIdFromFrontmatter(document, key, "territory_");
    }

    private String entityIdFromFrontmatter(BootstrapDocument document, String key, String prefix) {
        String value = frontmatterString(document, key);
        if (value == null || !value.startsWith(prefix)) {
            return null;
        }
        return value;
    }
}
