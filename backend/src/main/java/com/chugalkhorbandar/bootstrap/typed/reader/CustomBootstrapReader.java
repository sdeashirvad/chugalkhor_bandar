package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.CustomBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CustomBootstrapReader extends AbstractBootstrapTypedReader<CustomBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames("Category", "Title", "Description");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.CUSTOMS;
    }

    @Override
    public CustomBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new CustomBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "Category"),
                optional(document, "Title"),
                optional(document, "Description"),
                unmapped(document, KNOWN));
    }
}
