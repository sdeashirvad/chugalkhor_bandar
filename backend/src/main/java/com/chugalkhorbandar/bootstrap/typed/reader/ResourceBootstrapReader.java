package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.ResourceBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ResourceBootstrapReader extends AbstractBootstrapTypedReader<ResourceBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "ID", "Name", "Category", "Description", "Produced By", "Lifecycle", "Notes");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.RESOURCES;
    }

    @Override
    public ResourceBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new ResourceBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "ID"),
                optional(document, "Name"),
                optional(document, "Category"),
                optional(document, "Description"),
                optional(document, "Produced By"),
                optional(document, "Lifecycle"),
                optional(document, "Notes"),
                unmapped(document, KNOWN));
    }
}
