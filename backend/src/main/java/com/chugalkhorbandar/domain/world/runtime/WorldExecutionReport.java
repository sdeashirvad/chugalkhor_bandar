package com.chugalkhorbandar.domain.world.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record WorldExecutionReport(
        int executedCommandCount,
        long durationMillis,
        List<String> failures,
        List<String> warnings,
        Map<String, Integer> statistics,
        boolean success) {

    public WorldExecutionReport {
        failures = List.copyOf(failures);
        warnings = List.copyOf(warnings);
        statistics = Map.copyOf(statistics);
    }

    public String toSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("World Execution").append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("Executed Commands . ").append(executedCommandCount).append(System.lineSeparator());
        builder.append("Duration .......... ").append(durationMillis).append(" ms").append(System.lineSeparator());
        builder.append("Success ........... ").append(success).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        statistics.forEach((label, count) -> builder.append(label)
                .append(" ")
                .append(".".repeat(Math.max(1, 18 - label.length())))
                .append(" ")
                .append(count)
                .append(System.lineSeparator()));
        builder.append(System.lineSeparator());
        builder.append("Failures . ").append(failures.size()).append(System.lineSeparator());
        builder.append("Warnings . ").append(warnings.size());
        return builder.toString();
    }
}
