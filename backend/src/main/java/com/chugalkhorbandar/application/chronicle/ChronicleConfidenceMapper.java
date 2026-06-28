package com.chugalkhorbandar.application.chronicle;

public final class ChronicleConfidenceMapper {

    private ChronicleConfidenceMapper() {}

    public static ChronicleConfidence fromInboxConfidence(double confidence, ChronicleWriterProperties properties) {
        if (confidence >= 0.85) {
            return ChronicleConfidence.OFFICIAL;
        }
        if (confidence >= 0.55) {
            return ChronicleConfidence.LIKELY;
        }
        return ChronicleConfidence.RUMOR;
    }

    public static ChronicleConfidence withDefault(double confidence, ChronicleWriterProperties properties) {
        ChronicleConfidence mapped = fromInboxConfidence(confidence, properties);
        if (mapped == ChronicleConfidence.RUMOR && properties.getDefaultConfidence() != ChronicleConfidence.RUMOR) {
            return properties.getDefaultConfidence();
        }
        return mapped;
    }
}
