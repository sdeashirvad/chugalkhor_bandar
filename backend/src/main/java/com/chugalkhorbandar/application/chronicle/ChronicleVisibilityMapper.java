package com.chugalkhorbandar.application.chronicle;

public final class ChronicleVisibilityMapper {

    private ChronicleVisibilityMapper() {}

    public static ChronicleVisibility resolve(
            ChronicleCategory category, ChronicleWriterProperties properties) {
        return switch (category) {
            case PROMISE, PERSONAL, PREFERENCE -> ChronicleVisibility.PRIVATE;
            case STORY, WORLD, EVENT, DISCOVERY -> ChronicleVisibility.PUBLIC;
            case RELATIONSHIP -> ChronicleVisibility.PRIVATE;
            case CUSTOM -> properties.getDefaultVisibility();
        };
    }
}
