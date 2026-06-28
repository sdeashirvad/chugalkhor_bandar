package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.StoryBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class StoryBootstrapReader extends AbstractBootstrapTypedReader<StoryBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "Summary",
            "Participants",
            "Major Places",
            "Beginning",
            "Key Events",
            "Ending",
            "Canonical Consequences",
            "Linked Characters",
            "Linked Places",
            "Linked Organizations",
            "Linked Stories",
            "Notes");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.STORY;
    }

    @Override
    public StoryBootstrapSpec read(BootstrapDocument document) {
        return new StoryBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                require(document, "Summary"),
                optional(document, "Participants"),
                optional(document, "Major Places"),
                optional(document, "Beginning"),
                optional(document, "Key Events"),
                optional(document, "Ending"),
                optional(document, "Canonical Consequences"),
                optional(document, "Linked Characters"),
                optional(document, "Linked Places"),
                optional(document, "Linked Organizations"),
                optional(document, "Linked Stories"),
                optional(document, "Notes"),
                unmapped(document, KNOWN));
    }
}
