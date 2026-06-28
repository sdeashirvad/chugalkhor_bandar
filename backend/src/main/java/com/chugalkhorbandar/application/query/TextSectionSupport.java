package com.chugalkhorbandar.application.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextSectionSupport {

    private static final Pattern TABLE_SPECIES_PATTERN =
            Pattern.compile("\\|\\s*Species\\s*\\|\\s*([^|\\n]+)\\s*\\|", Pattern.CASE_INSENSITIVE);

    private TextSectionSupport() {}

    public static List<String> parseListItems(String sectionText) {
        if (sectionText == null || sectionText.isBlank()) {
            return List.of();
        }
        List<String> items = new ArrayList<>();
        for (String line : sectionText.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("- ")) {
                items.add(trimmed.substring(2).trim());
            }
        }
        return List.copyOf(items);
    }

    public static String extractSpecies(Map<String, String> sections) {
        String basicInformation = sections.get("basicInformation");
        if (basicInformation != null) {
            Matcher matcher = TABLE_SPECIES_PATTERN.matcher(basicInformation);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return sections.getOrDefault("species", "");
    }

    public static List<String> extractPublicFacts(Map<String, String> sections) {
        String reputation = sections.get("publicReputation");
        if (reputation == null || reputation.isBlank()) {
            return List.of();
        }
        return parseListItems(reputation);
    }

    public static Map<String, String> publicSections(Map<String, String> sections) {
        Map<String, String> publicSections = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : sections.entrySet()) {
            if (!"secrets".equals(entry.getKey())) {
                publicSections.put(entry.getKey(), entry.getValue());
            }
        }
        return Map.copyOf(publicSections);
    }
}
