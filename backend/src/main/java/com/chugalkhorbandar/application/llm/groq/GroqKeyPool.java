package com.chugalkhorbandar.application.llm.groq;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class GroqKeyPool {

    private final List<String> keys;
    private final AtomicInteger sequence = new AtomicInteger(0);

    public GroqKeyPool(List<String> configuredKeys) {
        List<String> loaded = new ArrayList<>();
        if (configuredKeys != null) {
            for (String key : configuredKeys) {
                if (key != null && !key.isBlank()) {
                    loaded.add(key.trim());
                }
            }
        }
        this.keys = List.copyOf(loaded);
    }

    public int keyCount() {
        return keys.size();
    }

    public GroqKeySelection acquireKey() {
        if (keys.isEmpty()) {
            throw new IllegalStateException("No Groq API keys configured");
        }
        int slot = Math.floorMod(sequence.getAndIncrement(), keys.size());
        return selectionForSlot(slot);
    }

    public Optional<GroqKeySelection> alternateKey(int usedDisplayIndex) {
        if (keys.size() < 2) {
            return Optional.empty();
        }
        int usedSlot = usedDisplayIndex - 1;
        if (usedSlot < 0 || usedSlot >= keys.size()) {
            return Optional.empty();
        }
        int alternateSlot = (usedSlot + 1) % keys.size();
        if (alternateSlot == usedSlot) {
            return Optional.empty();
        }
        return Optional.of(selectionForSlot(alternateSlot));
    }

    private GroqKeySelection selectionForSlot(int slot) {
        return new GroqKeySelection(slot + 1, keys.get(slot));
    }

    public record GroqKeySelection(int displayIndex, String apiKey) {}
}
