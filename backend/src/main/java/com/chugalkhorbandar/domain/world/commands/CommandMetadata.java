package com.chugalkhorbandar.domain.world.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class CommandMetadata {

    private static final CommandMetadata EMPTY = new CommandMetadata(Map.of());

    private final Map<String, String> values;

    private CommandMetadata(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    public static CommandMetadata empty() {
        return EMPTY;
    }

    public static CommandMetadata of(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return empty();
        }
        return new CommandMetadata(values);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, String> asMap() {
        return values;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(values.get(key));
    }

    public CommandMetadata with(String key, String value) {
        Objects.requireNonNull(key, "key");
        Map<String, String> merged = new LinkedHashMap<>(values);
        if (value == null) {
            merged.remove(key);
        } else {
            merged.put(key, value);
        }
        return new CommandMetadata(merged);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CommandMetadata that)) {
            return false;
        }
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return values.toString();
    }

    public static final class Builder {

        private final Map<String, String> values = new LinkedHashMap<>();

        public Builder put(String key, String value) {
            if (key != null && value != null) {
                values.put(key, value);
            }
            return this;
        }

        public Builder putAll(Map<String, String> entries) {
            if (entries != null) {
                entries.forEach((key, value) -> {
                    if (key != null && value != null) {
                        values.put(key, value);
                    }
                });
            }
            return this;
        }

        public CommandMetadata build() {
            return CommandMetadata.of(values);
        }
    }
}
