package com.chugalkhorbandar.application.chronicle;

public final class ChronicleBodyTemplateRenderer {

    private ChronicleBodyTemplateRenderer() {}

    public static String render(
            ChronicleCategory category,
            String ownerName,
            String recipientName,
            String summary) {
        String safeSummary = summary == null || summary.isBlank() ? "something unspoken" : summary.trim();
        String owner = ownerName == null || ownerName.isBlank() ? "someone" : ownerName;
        String recipient = recipientName == null || recipientName.isBlank() ? "a companion" : recipientName;

        return switch (category) {
            case PROMISE -> "Bandar promised " + recipient + " that " + safeSummary + ".";
            case STORY -> "A tale entered the Jungle chronicle: " + safeSummary + ".";
            case PREFERENCE -> owner + " expressed a preference remembered by Bandar: " + safeSummary + ".";
            case PERSONAL -> "Bandar committed to memory, on behalf of " + owner + ": " + safeSummary + ".";
            case RELATIONSHIP -> "A bond in the Jungle was noted between " + owner + " and " + recipient + ": "
                    + safeSummary + ".";
            case DISCOVERY -> "A discovery was recorded in the Jungle: " + safeSummary + ".";
            case EVENT -> "An event passed through the Jungle: " + safeSummary + ".";
            case WORLD -> "The world shifted, and Bandar wrote it down: " + safeSummary + ".";
            case CUSTOM -> safeSummary.endsWith(".") ? safeSummary : safeSummary + ".";
        };
    }

    public static String renderTitle(ChronicleCategory category, String summary) {
        String prefix =
                switch (category) {
                    case PROMISE -> "Promise";
                    case STORY -> "Story";
                    case PREFERENCE -> "Preference";
                    case PERSONAL -> "Personal Memory";
                    case RELATIONSHIP -> "Relationship";
                    case DISCOVERY -> "Discovery";
                    case EVENT -> "Event";
                    case WORLD -> "World Record";
                    case CUSTOM -> "Chronicle";
                };
        String trimmed = summary == null ? "" : summary.trim();
        if (trimmed.length() <= 72) {
            return prefix + ": " + trimmed;
        }
        return prefix + ": " + trimmed.substring(0, 69).trim() + "...";
    }

    public static String templateName(ChronicleCategory category) {
        return category.name().toLowerCase() + "-narrative-v1";
    }
}
