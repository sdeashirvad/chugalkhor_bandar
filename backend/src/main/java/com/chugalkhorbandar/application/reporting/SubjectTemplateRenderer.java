package com.chugalkhorbandar.application.reporting;

public final class SubjectTemplateRenderer {

    private SubjectTemplateRenderer() {}

    public static String render(String template, ReportTemplateContext context) {
        if (template == null || template.isBlank()) {
            return "";
        }
        return template
                .replace("{date}", safe(context.date()))
                .replace("{conversationCount}", safe(context.conversations()))
                .replace("{promoted}", safe(context.promoted()))
                .replace("{discarded}", safe(context.discarded()))
                .replace("{pending}", safe(context.pending()));
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
