package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.PromptProfileBootstrapSpec;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PromptProfileBootstrapReader extends AbstractBootstrapTypedReader<PromptProfileBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "Identity",
            "Core Personality",
            "Speaking Style",
            "Behavior",
            "Forbidden Behaviors",
            "Narration Rules");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.PROMPT;
    }

    @Override
    public PromptProfileBootstrapSpec read(BootstrapDocument document) {
        return new PromptProfileBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                require(document, "Identity"),
                optional(document, "Core Personality"),
                optional(document, "Speaking Style"),
                optional(document, "Behavior"),
                optional(document, "Forbidden Behaviors"),
                optional(document, "Narration Rules"),
                unmapped(document, KNOWN));
    }
}
