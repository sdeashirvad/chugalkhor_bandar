package com.chugalkhorbandar.bootstrap.typed.spec;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;

public interface BootstrapTypedSpec {

    String id();

    String title();

    Path sourcePath();

    String status();

    String version();

    DocumentType documentType();
}
