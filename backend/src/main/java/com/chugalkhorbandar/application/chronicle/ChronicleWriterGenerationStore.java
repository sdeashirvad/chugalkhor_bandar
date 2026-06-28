package com.chugalkhorbandar.application.chronicle;

import java.util.Optional;

public class ChronicleWriterGenerationStore {

    private volatile ChronicleWriteResult latestResult;

    public void save(ChronicleWriteResult result) {
        this.latestResult = result;
    }

    public Optional<ChronicleWriteResult> getLatest() {
        return Optional.ofNullable(latestResult);
    }
}
