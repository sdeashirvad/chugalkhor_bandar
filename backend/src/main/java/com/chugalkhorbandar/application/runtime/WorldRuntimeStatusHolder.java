package com.chugalkhorbandar.application.runtime;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class WorldRuntimeStatusHolder {

    private volatile boolean ready;
    private volatile Instant runtimeStartedAt;

    public void markReady() {
        ready = true;
        runtimeStartedAt = Instant.now();
    }

    public boolean isReady() {
        return ready;
    }

    public Instant runtimeStartedAt() {
        return runtimeStartedAt;
    }
}
