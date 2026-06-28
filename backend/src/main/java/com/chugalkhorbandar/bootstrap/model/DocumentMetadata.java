package com.chugalkhorbandar.bootstrap.model;

import java.nio.file.Path;
import java.util.Map;

public record DocumentMetadata(
        String id, String title, String version, String status, Path filePath) {

    public static DocumentMetadata fromFrontmatter(Map<String, Object> frontmatter, Path filePath) {
        String title = stringValue(frontmatter.get("title"));
        if (title == null) {
            title = stringValue(frontmatter.get("name"));
        }
        return new DocumentMetadata(
                stringValue(frontmatter.get("id")),
                title,
                stringValue(frontmatter.get("version")),
                stringValue(frontmatter.get("status")),
                filePath);
    }

    private static String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }
}
