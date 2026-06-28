package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.RelationshipBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RelationshipBootstrapReader extends AbstractBootstrapTypedReader<RelationshipBootstrapSpec> {

    private static final Set<String> KNOWN =
            SectionReaderSupport.knownSectionNames("Relationship Type", "Characters", "Description", "Status");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.RELATIONSHIPS;
    }

    @Override
    public RelationshipBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new RelationshipBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "Relationship Type"),
                optional(document, "Characters"),
                optional(document, "Description"),
                optional(document, "Status"),
                unmapped(document, KNOWN));
    }
}
