package com.chugalkhorbandar.bootstrap.typed;

import java.nio.file.Path;

public class TypedReaderException extends RuntimeException {

    private final String documentId;
    private final Path sourcePath;

    public TypedReaderException(String message, String documentId, Path sourcePath) {
        super(message + " [id=" + documentId + ", path=" + sourcePath + "]");
        this.documentId = documentId;
        this.sourcePath = sourcePath;
    }

    public String getDocumentId() {
        return documentId;
    }

    public Path getSourcePath() {
        return sourcePath;
    }
}
