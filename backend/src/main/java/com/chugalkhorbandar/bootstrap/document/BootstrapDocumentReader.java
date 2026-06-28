package com.chugalkhorbandar.bootstrap.document;

import com.chugalkhorbandar.bootstrap.document.model.BootstrapDocument;
import com.chugalkhorbandar.bootstrap.document.model.DocumentBody;
import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import com.chugalkhorbandar.bootstrap.model.DocumentMetadata;
import com.chugalkhorbandar.bootstrap.parser.FrontmatterParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class BootstrapDocumentReader {

  private final FrontmatterParser frontmatterParser;
  private final MarkdownBodyParser bodyParser;
  private final DocumentTypeResolver typeResolver;

  public BootstrapDocumentReader() {
    this(new FrontmatterParser(), new MarkdownBodyParser(), new DocumentTypeResolver());
  }

  public BootstrapDocumentReader(
      FrontmatterParser frontmatterParser,
      MarkdownBodyParser bodyParser,
      DocumentTypeResolver typeResolver) {
    this.frontmatterParser = frontmatterParser;
    this.bodyParser = bodyParser;
    this.typeResolver = typeResolver;
  }

  public BootstrapDocument read(Path bootstrapRoot, Path filePath) throws IOException {
    String originalMarkdown = Files.readString(filePath);
    String normalized = originalMarkdown.replace("\r\n", "\n").replace('\r', '\n');

    Map<String, Object> frontmatter = frontmatterParser
        .parseFrontmatter(filePath)
        .orElseThrow(() -> new IllegalArgumentException("Missing frontmatter: " + filePath));

    DocumentMetadata metadata = DocumentMetadata.fromFrontmatter(frontmatter, filePath);
    DocumentBody body = bodyParser.parse(normalized);
    DocumentType documentType = typeResolver.resolve(bootstrapRoot, filePath);

    return new BootstrapDocument(metadata, frontmatter, body, normalized, filePath, documentType);
  }
}
