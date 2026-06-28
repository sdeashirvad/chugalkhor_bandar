package com.chugalkhorbandar.application.reporting;

import com.chugalkhorbandar.application.memory.consolidation.MemoryConsolidationReport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ReportContextBuilder {

    private final ReportingProperties properties;
    private final ObjectMapper objectMapper;

    public ReportContextBuilder(ReportingProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public ReportTemplateContext build(MemoryConsolidationReport report, List<String> attachmentNames) {
        JsonNode json = parseJson(report.jsonReport());
        String date = text(json, "date", report.startedAt().toString().substring(0, 10));
        String greeting = "Good morning, " + properties.getBranding().getGreetingName() + ".";
        String reflection = report.reflection() == null ? "" : report.reflection().trim();
        String reflectionSection = reflection.isBlank() ? "" : "Bandar Reflection\n\n" + reflection;
        String reflectionSectionHtml = reflection.isBlank()
                ? ""
                : "<h2 style=\"margin:0 0 12px;font-size:18px;color:#3d5a3e;\">Bandar Reflection</h2>"
                        + "<p style=\"margin:0 0 24px;line-height:1.7;color:#5a4f42;font-style:italic;\">"
                        + escapeHtml(reflection)
                        + "</p>";
        String closing = ClosingSelector.select(report.runId(), properties.getClosings());
        String attachmentsList = String.join("\n", attachmentNames.stream().map(name -> "- " + name).toList());
        String attachmentsListHtml = attachmentNames.stream()
                .map(name -> "<li>" + escapeHtml(name) + "</li>")
                .reduce("", String::concat);
        return new ReportTemplateContext(
                date,
                greeting,
                reflection,
                reflectionSection,
                reflectionSectionHtml,
                text(json, "conversations", String.valueOf(report.processed())),
                text(json, "artifacts", ""),
                text(json, "inboxItems", String.valueOf(report.processed())),
                text(json, "promoted", String.valueOf(report.promoted())),
                text(json, "discarded", String.valueOf(report.discarded())),
                text(json, "candidates", String.valueOf(report.candidateCount())),
                String.valueOf(report.pending()),
                text(json, "pendingPromises", ""),
                text(json, "unreadNotifications", ""),
                closing,
                attachmentsList,
                attachmentsListHtml,
                buildStatCards(json, report));
    }

    private static String buildStatCards(JsonNode json, MemoryConsolidationReport report) {
        List<String[]> stats = List.of(
                new String[] {"Conversations", text(json, "conversations", String.valueOf(report.processed()))},
                new String[] {"Artifacts", text(json, "artifacts", "")},
                new String[] {"Inbox", text(json, "inboxItems", String.valueOf(report.processed()))},
                new String[] {"Promoted", text(json, "promoted", String.valueOf(report.promoted()))},
                new String[] {"Discarded", text(json, "discarded", String.valueOf(report.discarded()))},
                new String[] {"Candidates", text(json, "candidates", String.valueOf(report.candidateCount()))},
                new String[] {"Promises", text(json, "pendingPromises", "")},
                new String[] {"Notifications", text(json, "unreadNotifications", "")});
        StringBuilder builder = new StringBuilder();
        for (String[] stat : stats) {
            builder.append("<td style=\"width:25%;padding:8px;vertical-align:top;\">")
                    .append("<div style=\"background:#f7f2ea;border:1px solid #e2d8c8;border-radius:8px;padding:12px;\">")
                    .append("<p style=\"margin:0;font-size:11px;text-transform:uppercase;letter-spacing:0.06em;color:#8a7f72;\">")
                    .append(escapeHtml(stat[0]))
                    .append("</p><p style=\"margin:6px 0 0;font-size:22px;color:#3d5a3e;\">")
                    .append(escapeHtml(stat[1]))
                    .append("</p></div></td>");
        }
        return builder.toString();
    }

    private JsonNode parseJson(String jsonReport) {
        try {
            return objectMapper.readTree(jsonReport == null || jsonReport.isBlank() ? "{}" : jsonReport);
        } catch (Exception exception) {
            return objectMapper.createObjectNode();
        }
    }

    private static String text(JsonNode json, String field, String fallback) {
        JsonNode node = json.get(field);
        if (node == null || node.isNull()) {
            return fallback == null ? "" : fallback;
        }
        return node.asText(fallback == null ? "" : fallback);
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
