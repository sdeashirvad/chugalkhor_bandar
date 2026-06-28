package com.chugalkhorbandar.bootstrap.document;

import com.chugalkhorbandar.bootstrap.document.model.DocumentType;
import java.nio.file.Path;

public final class DocumentTypeResolver {

  public DocumentType resolve(Path bootstrapRoot, Path filePath) {
    Path normalizedRoot = bootstrapRoot.toAbsolutePath().normalize();
    Path normalizedFile = filePath.toAbsolutePath().normalize();
    Path relative = normalizedRoot.relativize(normalizedFile);

    if (relative.getNameCount() > 1) {
      return switch (relative.getName(0).toString()) {
        case "characters" -> DocumentType.CHARACTER;
        case "stories" -> DocumentType.STORY;
        case "prompts" -> DocumentType.PROMPT;
        case "chronology" -> DocumentType.CHRONOLOGY;
        default -> DocumentType.REFERENCE;
      };
    }

    String stem = stripExtension(normalizedFile.getFileName().toString());
    return switch (stem) {
      case "canon" -> DocumentType.CANON;
      case "world-rules" -> DocumentType.WORLD_RULES;
      case "glossary" -> DocumentType.GLOSSARY;
      case "territories" -> DocumentType.TERRITORIES;
      case "places" -> DocumentType.PLACES;
      case "relationships" -> DocumentType.RELATIONSHIPS;
      case "organizations" -> DocumentType.ORGANIZATIONS;
      case "laws" -> DocumentType.LAWS;
      case "customs" -> DocumentType.CUSTOMS;
      case "resources" -> DocumentType.RESOURCES;
      case "objects" -> DocumentType.OBJECTS;
      case "family-tree" -> DocumentType.FAMILY_TREE;
      case "narrative-rules" -> DocumentType.NARRATIVE_RULES;
      default -> DocumentType.REFERENCE;
    };
  }

  private String stripExtension(String filename) {
    int index = filename.lastIndexOf('.');
    return index < 0 ? filename : filename.substring(0, index);
  }
}
