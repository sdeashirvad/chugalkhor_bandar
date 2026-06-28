package com.chugalkhorbandar.bootstrap.compiler.command;

import com.chugalkhorbandar.bootstrap.typed.spec.ChronologyTimelineItem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record CreateChronologyCommand(
        String commandId,
        int executionOrder,
        String sourceDocumentId,
        Path sourcePath,
        String chronologyId,
        String title,
        List<ChronologyTimelineItem> timelineItems,
        Map<String, String> sections,
        Map<String, String> metadata)
        implements BootstrapCommand {

    public CreateChronologyCommand {
        timelineItems = List.copyOf(timelineItems);
    }

    @Override
    public String commandType() {
        return "CreateChronology";
    }
}
