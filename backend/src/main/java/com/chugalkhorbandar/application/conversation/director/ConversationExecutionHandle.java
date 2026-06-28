package com.chugalkhorbandar.application.conversation.director;

import java.util.concurrent.atomic.AtomicBoolean;

public final class ConversationExecutionHandle {

    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private volatile String interruptionReason;

    void cancel(String reason) {
        cancelled.set(true);
        interruptionReason = reason;
    }

    boolean isCancelled() {
        return cancelled.get();
    }

    String interruptionReason() {
        return interruptionReason;
    }
}
