package com.chugalkhorbandar.bootstrap.typed.spec;



import com.chugalkhorbandar.bootstrap.document.model.DocumentType;

import java.nio.file.Path;

import java.util.Map;



public record PlaceBootstrapSpec(

        String id,

        String title,

        Path sourcePath,

        String status,

        String version,

        DocumentType documentType,

        String sourceDocumentId,

        String type,

        String description,

        String currentOwner,

        String locatedIn,

        String connectedPlaces,

        String importantLocations,

        String notes,

        Map<String, String> unmappedSections)

        implements BootstrapTypedSpec {}



