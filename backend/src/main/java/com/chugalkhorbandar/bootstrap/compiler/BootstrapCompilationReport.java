package com.chugalkhorbandar.bootstrap.compiler;

import java.util.List;
import java.util.Map;

public record BootstrapCompilationReport(
        Map<String, Integer> commandCountsByType,
        long compileDurationMillis,
        List<String> warnings,
        boolean success) {

    public BootstrapCompilationReport {
        commandCountsByType = Map.copyOf(commandCountsByType);
        warnings = List.copyOf(warnings);
    }

    public int totalCommands() {
        return commandCountsByType.values().stream().mapToInt(Integer::intValue).sum();
    }

    public String toSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Bootstrap Compilation\n\n");
        summary.append("Commands Generated\n\n");
        commandCountsByType.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> summary.append(formatCount(entry.getKey(), entry.getValue())));
        summary.append("\nTotal Commands . ").append(totalCommands()).append("\n\n");
        summary.append("Compilation Time . ").append(compileDurationMillis).append(" ms\n\n");
        summary.append("Warnings\n");
        if (warnings.isEmpty()) {
            summary.append("0\n");
        } else {
            warnings.forEach(warning -> summary.append("- ").append(warning).append("\n"));
        }
        return summary.toString();
    }

    private String formatCount(String label, int count) {
        String dots = ".".repeat(Math.max(1, 14 - label.length()));
        return label + " " + dots + " " + count + "\n";
    }
}
