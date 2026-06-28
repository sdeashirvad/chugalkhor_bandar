package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.TerritoryBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class TerritoryBootstrapReader extends AbstractBootstrapTypedReader<TerritoryBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "ID", "Name", "Capital", "Current Ruler", "Government", "Known Jungle Count", "History", "Goals", "Notes");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.TERRITORIES;
    }

    @Override
    public TerritoryBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new TerritoryBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "ID"),
                optional(document, "Name"),
                optional(document, "Capital"),
                optional(document, "Current Ruler"),
                optional(document, "Government"),
                optional(document, "Known Jungle Count"),
                optional(document, "History"),
                optional(document, "Goals"),
                optional(document, "Notes"),
                unmapped(document, KNOWN));
    }
}
