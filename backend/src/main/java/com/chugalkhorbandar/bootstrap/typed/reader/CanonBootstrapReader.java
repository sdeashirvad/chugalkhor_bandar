package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.CanonBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CanonBootstrapReader extends AbstractBootstrapTypedReader<CanonBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "Canonical Facts",
            "World Truths",
            "Stable Rules",
            "Important Lore References");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.CANON;
    }

    @Override
    public CanonBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new CanonBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "Canonical Facts"),
                optional(document, "World Truths"),
                optional(document, "Stable Rules"),
                optional(document, "Important Lore References"),
                unmapped(document, KNOWN));
    }
}
