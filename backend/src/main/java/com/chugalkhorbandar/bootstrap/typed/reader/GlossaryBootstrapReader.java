package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.GlossaryEntryBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class GlossaryBootstrapReader extends AbstractBootstrapTypedReader<GlossaryEntryBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames("Term", "Definition", "References");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.GLOSSARY;
    }

    @Override
    public GlossaryEntryBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new GlossaryEntryBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "Term"),
                optional(document, "Definition"),
                optional(document, "References"),
                unmapped(document, KNOWN));
    }
}
