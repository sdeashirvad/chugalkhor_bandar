package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.OrganizationBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class OrganizationBootstrapReader extends AbstractBootstrapTypedReader<OrganizationBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "ID", "Name", "Type", "Leader", "Headquarters", "Purpose", "Known Members", "Rules", "Notes");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.ORGANIZATIONS;
    }

    @Override
    public OrganizationBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new OrganizationBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "ID"),
                optional(document, "Name"),
                optional(document, "Type"),
                optional(document, "Leader"),
                optional(document, "Headquarters"),
                optional(document, "Purpose"),
                optional(document, "Known Members"),
                optional(document, "Rules"),
                optional(document, "Notes"),
                unmapped(document, KNOWN));
    }
}
