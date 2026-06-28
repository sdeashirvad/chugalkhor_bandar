package com.chugalkhorbandar.application.world.living;

import java.time.LocalDate;

public final class WorldEventIdFactory {

    private WorldEventIdFactory() {}

    public static String create(WorldEventType type, LocalDate effectiveDate, String key) {
        String normalizedKey = key == null ? "global" : key.replaceAll("[^a-zA-Z0-9_-]", "-");
        return "evt-" + type.name().toLowerCase() + "-" + effectiveDate + "-" + normalizedKey;
    }
}
