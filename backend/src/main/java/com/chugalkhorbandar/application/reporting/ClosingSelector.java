package com.chugalkhorbandar.application.reporting;

import java.util.List;

public final class ClosingSelector {

    private ClosingSelector() {}

    public static String select(String reportId, List<String> closings) {
        if (closings == null || closings.isEmpty()) {
            return "Until tomorrow.";
        }
        int index = Math.floorMod(reportId == null ? 0 : reportId.hashCode(), closings.size());
        return closings.get(index);
    }
}
