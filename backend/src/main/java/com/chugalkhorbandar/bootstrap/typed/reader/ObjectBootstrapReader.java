package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.ObjectBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ObjectBootstrapReader extends AbstractBootstrapTypedReader<ObjectBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "ID", "Name", "Type", "Owner", "Location", "Description", "History", "Rules", "Notes");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.OBJECTS;
    }

    @Override
    public ObjectBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new ObjectBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "ID"),
                optional(document, "Name"),
                optional(document, "Type"),
                optional(document, "Owner"),
                optional(document, "Location"),
                optional(document, "Description"),
                optional(document, "History"),
                optional(document, "Rules"),
                optional(document, "Notes"),
                unmapped(document, KNOWN));
    }
}
