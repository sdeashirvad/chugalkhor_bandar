package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.LawBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class LawBootstrapReader extends AbstractBootstrapTypedReader<LawBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames("Law Number", "Title", "Description");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.LAWS;
    }

    @Override
    public LawBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new LawBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "Law Number"),
                optional(document, "Title"),
                optional(document, "Description"),
                unmapped(document, KNOWN));
    }
}
