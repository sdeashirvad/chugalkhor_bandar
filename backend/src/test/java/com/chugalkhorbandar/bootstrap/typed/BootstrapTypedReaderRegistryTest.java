package com.chugalkhorbandar.bootstrap.typed;

import static org.assertj.core.api.Assertions.assertThat;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.typed.reader.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class BootstrapTypedReaderRegistryTest {

    private final BootstrapTypedReaderRegistry registry = new BootstrapTypedReaderRegistry(List.of(
            new CharacterBootstrapReader(),
            new StoryBootstrapReader(),
            new PlaceBootstrapReader(),
            new TerritoryBootstrapReader(),
            new OrganizationBootstrapReader(),
            new ResourceBootstrapReader(),
            new ObjectBootstrapReader(),
            new RelationshipBootstrapReader(),
            new LawBootstrapReader(),
            new CustomBootstrapReader(),
            new GlossaryBootstrapReader(),
            new PromptProfileBootstrapReader(),
            new CanonBootstrapReader(),
            new WorldRulesBootstrapReader(),
            new ChronologyBootstrapReader()));

    @Test
    void selectsReaderByDocumentType() {
        assertThat(registry.isSupported(DocumentType.CHARACTER)).isTrue();
        assertThat(registry.isSupported(DocumentType.STORY)).isTrue();
        assertThat(registry.isSupported(DocumentType.REFERENCE)).isFalse();
        assertThat(registry.findReader(DocumentType.CHARACTER)).isPresent();
    }
}
