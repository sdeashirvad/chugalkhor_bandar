package com.chugalkhorbandar.domain.world.ports.query;

import java.time.Instant;

public record TimelineQuery(String chronologyId, Instant after, Instant before) {

    public static TimelineQuery all() {
        return new TimelineQuery(null, null, null);
    }

    public static TimelineQuery forChronology(String chronologyId) {
        return new TimelineQuery(chronologyId, null, null);
    }

    public static TimelineQuery between(Instant after, Instant before) {
        return new TimelineQuery(null, after, before);
    }
}
