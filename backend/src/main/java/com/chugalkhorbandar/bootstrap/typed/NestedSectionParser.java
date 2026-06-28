package com.chugalkhorbandar.bootstrap.typed;

import java.util.LinkedHashMap;
import java.util.Map;

public final class NestedSectionParser {

    private NestedSectionParser() {}

    public static Map<String, String> parse(String content) {
        if (content == null || content.isBlank()) {
            return Map.of();
        }
        Map<String, String> sections = new LinkedHashMap<>();
        String currentTitle = null;
        StringBuilder currentContent = new StringBuilder();
        for (String line : content.split("\n", -1)) {
            if (line.startsWith("## ")) {
                flush(sections, currentTitle, currentContent);
                currentTitle = line.substring(3).trim();
                currentContent = new StringBuilder();
                continue;
            }
            appendLine(currentContent, line);
        }
        flush(sections, currentTitle, currentContent);
        return Map.copyOf(sections);
    }

    public static String extractEntityId(Map<String, String> sections, String prefix) {
        String raw = sections.get("ID");
        if (raw == null) {
            return null;
        }
        String cleaned = raw.trim().replace("`", "").trim();
        return cleaned.startsWith(prefix) ? cleaned : null;
    }

    private static void flush(Map<String, String> sections, String title, StringBuilder content) {
        if (title == null && content.isEmpty()) {
            return;
        }
        sections.put(title == null ? "" : title, content.toString().strip());
    }

    private static void appendLine(StringBuilder builder, String line) {
        if (!builder.isEmpty()) {
            builder.append('\n');
        }
        builder.append(line);
    }
}
