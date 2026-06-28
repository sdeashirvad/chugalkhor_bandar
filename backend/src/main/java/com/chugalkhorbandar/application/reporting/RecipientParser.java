package com.chugalkhorbandar.application.reporting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RecipientParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipientParser.class);
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private RecipientParser() {}

    public static List<String> parse(String rawRecipients) {
        if (rawRecipients == null || rawRecipients.isBlank()) {
            return List.of();
        }
        String normalized = rawRecipients.replace(';', ',');
        String[] parts = normalized.split(",");
        List<String> valid = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (EMAIL_PATTERN.matcher(trimmed).matches()) {
                valid.add(trimmed);
            } else {
                LOGGER.warn("Skipping invalid report recipient: {}", trimmed);
            }
        }
        return List.copyOf(valid);
    }
}
