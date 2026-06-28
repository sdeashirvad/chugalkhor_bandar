package com.chugalkhorbandar.domain.world.runtime;

import com.chugalkhorbandar.domain.world.commands.WorldCommand;
import java.util.List;

public final class WorldCommandHandlerRegistry {

    private final List<WorldCommandHandler<?>> handlers;

    public WorldCommandHandlerRegistry(List<WorldCommandHandler<?>> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            throw new WorldExecutionException("At least one world command handler is required");
        }
        this.handlers = List.copyOf(handlers);
    }

    public static WorldCommandHandlerRegistry createDefault() {
        return new WorldCommandHandlerRegistry(WorldCommandHandlers.all());
    }

    public WorldCommandHandler<?> resolve(WorldCommand command) {
        if (command == null) {
            throw new WorldExecutionException("Cannot resolve handler for null command");
        }
        return handlers.stream()
                .filter(handler -> handler.supports(command))
                .findFirst()
                .orElseThrow(() -> new WorldExecutionException(
                        "No handler registered for command type: " + command.commandType()));
    }

    public WorldState dispatch(WorldState current, WorldCommand command) {
        WorldCommandHandler<?> handler = resolve(command);
        return dispatch(handler, current, command);
    }

    @SuppressWarnings("unchecked")
    private static <T extends WorldCommand> WorldState dispatch(
            WorldCommandHandler<?> handler, WorldState current, WorldCommand command) {
        WorldState next = ((WorldCommandHandler<T>) handler).handle(current, (T) command);
        if (next == null) {
            throw new WorldExecutionException("Handler returned null state for command: " + command.commandId());
        }
        return next;
    }
}
