package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentSection;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.SectionReaderSupport;
import com.chugalkhorbandar.bootstrap.typed.spec.ChronologyBootstrapSpec;
import com.chugalkhorbandar.bootstrap.typed.spec.ChronologyTimelineItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ChronologyBootstrapReader extends AbstractBootstrapTypedReader<ChronologyBootstrapSpec> {

    private static final Set<String> KNOWN = SectionReaderSupport.knownSectionNames(
            "Ancient Era", "Medieval Era", "Modern Era");

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.CHRONOLOGY;
    }

    @Override
    public ChronologyBootstrapSpec read(BootstrapDocument document) {
        SectionReaderSupport.requireAtLeastOneSection(document);

        List<ChronologyTimelineItem> items = new ArrayList<>();
        for (DocumentSection section : document.getSections()) {
            if (KNOWN.contains(section.title())) {
                items.add(new ChronologyTimelineItem(
                        section.title(), null, section.title(), section.content(), null));
            } else {
                items.add(parseTimelineItem(section));
            }
        }

        return new ChronologyBootstrapSpec(
                id(document),
                title(document),
                sourcePath(document),
                status(document),
                version(document),
                documentType(document),
                List.copyOf(items),
                unmapped(document, KNOWN));
    }

    private ChronologyTimelineItem parseTimelineItem(DocumentSection section) {
        String content = section.content();
        String linkedStory = extractLinkedStory(content);
        return new ChronologyTimelineItem(null, section.title(), section.title(), content, linkedStory);
    }

    private String extractLinkedStory(String content) {
        for (String line : content.split("\n")) {
            if (line.toLowerCase().startsWith("linked story:")) {
                return line.substring("linked story:".length()).trim();
            }
        }
        return null;
    }
}
