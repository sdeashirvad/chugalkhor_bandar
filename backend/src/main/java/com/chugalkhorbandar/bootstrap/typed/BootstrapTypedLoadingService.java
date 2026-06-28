package com.chugalkhorbandar.bootstrap.typed;

import com.chugalkhorbandar.bootstrap.document.BootstrapDocumentRepository;
import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.reader.PlaceBootstrapReader;
import com.chugalkhorbandar.bootstrap.typed.spec.*;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class BootstrapTypedLoadingService {

    private static final Set<DocumentType> IGNORED_TYPES =
            Set.of(DocumentType.REFERENCE, DocumentType.FAMILY_TREE, DocumentType.NARRATIVE_RULES);

    private final BootstrapTypedReaderRegistry registry;

    public BootstrapTypedLoadingService(BootstrapTypedReaderRegistry registry) {
        this.registry = registry;
    }

    public BootstrapTypedWorld load(BootstrapDocumentRepository repository) {
        BootstrapTypedWorld.Builder builder = BootstrapTypedWorld.builder();

        for (BootstrapDocument document : repository.findAll()) {
            if (IGNORED_TYPES.contains(document.documentType())) {
                continue;
            }
            if (!registry.isSupported(document.documentType())) {
                continue;
            }
            route(document, builder);
        }

        return builder.build();
    }

    private void route(BootstrapDocument document, BootstrapTypedWorld.Builder builder) {
        switch (document.documentType()) {
            case CHARACTER -> builder.addCharacter(registry.read(document));
            case STORY -> builder.addStory(registry.read(document));
            case PLACES -> {
                PlaceBootstrapReader placeReader = (PlaceBootstrapReader) registry.findReader(DocumentType.PLACES).orElseThrow();
                placeReader.readAll(document).forEach(builder::addPlace);
            }
            case TERRITORIES -> builder.addTerritory(registry.read(document));
            case ORGANIZATIONS -> builder.addOrganization(registry.read(document));
            case RESOURCES -> builder.addResource(registry.read(document));
            case OBJECTS -> builder.addObject(registry.read(document));
            case RELATIONSHIPS -> builder.addRelationship(registry.read(document));
            case LAWS -> builder.addLaw(registry.read(document));
            case CUSTOMS -> builder.addCustom(registry.read(document));
            case GLOSSARY -> builder.addGlossaryEntry(registry.read(document));
            case PROMPT -> builder.addPromptProfile(registry.read(document));
            case CANON -> builder.addCanon(registry.read(document));
            case WORLD_RULES -> builder.addWorldRules(registry.read(document));
            case CHRONOLOGY -> builder.addChronology(registry.read(document));
            default -> { }
        }
    }
}
