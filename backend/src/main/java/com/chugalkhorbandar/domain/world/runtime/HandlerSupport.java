package com.chugalkhorbandar.domain.world.runtime;

import java.util.Map;

final class HandlerSupport {

    private HandlerSupport() {}

    static void ensureUnique(Map<String, ?> current, String id, String collectionName) {
        if (current.containsKey(id)) {
            throw new WorldExecutionException(
                    "Duplicate runtime id in " + collectionName + ": " + id);
        }
    }

    static <T> T requirePresent(Map<String, T> current, String id, String collectionName) {
        T value = current.get(id);
        if (value == null) {
            throw new WorldExecutionException("Missing runtime id in " + collectionName + ": " + id);
        }
        return value;
    }

    static boolean isBootstrapType(com.chugalkhorbandar.domain.world.commands.WorldCommand command, String type) {
        return command.metadata().get("bootstrapCommandType").map(type::equals).orElse(false);
    }
}
