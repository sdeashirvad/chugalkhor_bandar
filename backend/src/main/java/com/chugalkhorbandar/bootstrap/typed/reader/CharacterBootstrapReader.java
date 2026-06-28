package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.CharacterBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CharacterBootstrapReader extends AbstractBootstrapTypedReader<CharacterBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "Summary",
            "Basic Information",
            "Titles",
            "Roles",
            "Personality",
            "History",
            "Daily Routine",
            "Relationships",
            "Known Preferences",
            "Abilities",
            "Responsibilities",
            "Assets",
            "Public Reputation",
            "Secrets",
            "Notes");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.CHARACTER;
    }

    @Override
    public CharacterBootstrapSpec read(BootstrapDocument document) {
        return new CharacterBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                require(document, "Summary"),
                optional(document, "Basic Information"),
                optional(document, "Titles"),
                optional(document, "Roles"),
                optional(document, "Personality"),
                optional(document, "History"),
                optional(document, "Daily Routine"),
                optional(document, "Relationships"),
                optional(document, "Known Preferences"),
                optional(document, "Abilities"),
                optional(document, "Responsibilities"),
                optional(document, "Assets"),
                optional(document, "Public Reputation"),
                optional(document, "Secrets"),
                optional(document, "Notes"),
                placeIdFromFrontmatter(document, "current_place"),
                territoryIdFromFrontmatter(document, "home_territory"),
                unmapped(document, KNOWN));
    }
}
