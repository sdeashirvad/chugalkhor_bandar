package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.WorldRulesBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class WorldRulesBootstrapReader extends AbstractBootstrapTypedReader<WorldRulesBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "What Is Canon",
            "What Can Change",
            "Contradiction Rules",
            "Secrecy Rules",
            "Preferences Rules",
            "Title Rules",
            "Death Rules",
            "Story Continuity Rules");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.WORLD_RULES;
    }

    @Override
    public WorldRulesBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);
        return new WorldRulesBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                optional(document, "What Is Canon"),
                optional(document, "What Can Change"),
                optional(document, "Contradiction Rules"),
                optional(document, "Secrecy Rules"),
                optional(document, "Preferences Rules"),
                optional(document, "Title Rules"),
                optional(document, "Death Rules"),
                optional(document, "Story Continuity Rules"),
                unmapped(document, KNOWN));
    }
}
