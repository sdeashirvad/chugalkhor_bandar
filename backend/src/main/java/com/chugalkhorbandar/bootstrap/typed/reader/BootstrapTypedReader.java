package com.chugalkhorbandar.bootstrap.typed.reader;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;

public interface BootstrapTypedReader<T> {

    boolean supports(DocumentType type);

    T read(BootstrapDocument document);
}
