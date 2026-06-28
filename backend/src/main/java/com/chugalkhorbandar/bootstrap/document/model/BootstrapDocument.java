package com.chugalkhorbandar.bootstrap.document.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.chugalkhorbandar.bootstrap.model.DocumentMetadata;
import java.util.Map;

public final class BootstrapDocument {

    private final DocumentMetadata metadata;
    private final Map<String, Object> frontmatter;
    private final String heading;
    private final List<DocumentSection> sections;
    private final String originalMarkdown;
    private final Path sourcePath;
    private final DocumentType documentType;

    public BootstrapDocument(
            DocumentMetadata metadata,
            Map<String, Object> frontmatter,
            DocumentBody body,
            String originalMarkdown,
            Path sourcePath,
            DocumentType documentType) {
        this.metadata = metadata;
        this.frontmatter = Map.copyOf(frontmatter);
        this.heading = body.heading();
        this.sections = List.copyOf(body.sections());
        this.originalMarkdown = originalMarkdown;
        this.sourcePath = sourcePath;
        this.documentType = documentType;
    }

    public DocumentMetadata metadata() {
        return metadata;
    }

    public Map<String, Object> frontmatter() {
        return frontmatter;
    }

    public String heading() {
        return heading;
    }

    public DocumentBody body() {
        return new DocumentBody(heading, sections);
    }

    public List<DocumentSection> getSections() {
        return sections;
    }

    public String originalMarkdown() {
        return originalMarkdown;
    }

    public Path sourcePath() {
        return sourcePath;
    }

    public DocumentType documentType() {
        return documentType;
    }

    public Optional<DocumentSection> findSection(String title) {
        return sections.stream()
                .filter(section -> section.title().equalsIgnoreCase(title))
                .findFirst();
    }

    public boolean hasSection(String title) {
        return findSection(title).isPresent();
    }

    public Optional<String> getContentOptional(String title) {
        return findSection(title).map(DocumentSection::content);
    }

    public String getContent(String title) {
        return getContentOptional(title).orElse(null);
    }
}
