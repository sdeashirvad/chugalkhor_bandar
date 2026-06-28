package com.chugalkhorbandar.bootstrap.document;

import com.chugalkhorbandar.bootstrap.document.model.DocumentBody;
import com.chugalkhorbandar.bootstrap.document.model.DocumentSection;
import java.util.ArrayList;
import java.util.List;

public final class MarkdownBodyParser {

  private static final String H1_PREFIX = "# ";
  private static final String H2_PREFIX = "## ";

  public DocumentBody parse(String markdown) {
    String normalized = normalizeLineEndings(markdown);
    String body = stripFrontmatter(normalized);

    String heading = null;
    List<DocumentSection> sections = new ArrayList<>();
    StringBuilder currentContent = new StringBuilder();
    String currentSectionTitle = null;
    boolean headingFound = false;

    for (String line : body.split("\n", -1)) {
      if (line.startsWith(H2_PREFIX)) {
        flushSection(sections, currentSectionTitle, currentContent);
        currentSectionTitle = line.substring(H2_PREFIX.length()).trim();
        currentContent = new StringBuilder();
        continue;
      }

      if (line.startsWith(H1_PREFIX)) {
        String title = line.substring(H1_PREFIX.length()).trim();
        if (!headingFound) {
          heading = title;
          headingFound = true;
        } else {
          flushSection(sections, currentSectionTitle, currentContent);
          currentSectionTitle = title;
          currentContent = new StringBuilder();
        }
        continue;
      }

      appendLine(currentContent, line);
    }

    flushSection(sections, currentSectionTitle, currentContent);

    return new DocumentBody(heading == null ? "" : heading, List.copyOf(sections));
  }

  public int countH1Headings(String markdown) {
    String body = stripFrontmatter(normalizeLineEndings(markdown));
    int count = 0;
    for (String line : body.split("\n", -1)) {
      if (line.startsWith(H1_PREFIX) && !line.startsWith(H2_PREFIX)) {
        count++;
      }
    }
    return count;
  }

  public List<String> sectionTitles(String markdown) {
    DocumentBody body = parse(markdown);
    return body.sections().stream().map(DocumentSection::title).toList();
  }

  private void flushSection(
      List<DocumentSection> sections, String title, StringBuilder contentBuilder) {
    if (title == null && contentBuilder.isEmpty()) {
      return;
    }
    String sectionTitle = title == null ? "" : title;
    sections.add(new DocumentSection(sectionTitle, contentBuilder.toString().stripTrailing(), sections.size()));
  }

  private void appendLine(StringBuilder builder, String line) {
    if (!builder.isEmpty()) {
      builder.append('\n');
    }
    builder.append(line);
  }

  private String stripFrontmatter(String markdown) {
    if (!markdown.startsWith("---")) {
      return markdown;
    }
    int endIndex = markdown.indexOf("\n---", 3);
    if (endIndex < 0) {
      return markdown;
    }
    String remainder = markdown.substring(endIndex + 4);
    return remainder.startsWith("\n") ? remainder.substring(1) : remainder;
  }

  private String normalizeLineEndings(String markdown) {
    return markdown.replace("\r\n", "\n").replace('\r', '\n');
  }
}
