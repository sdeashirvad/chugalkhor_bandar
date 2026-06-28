package com.chugalkhorbandar.domain.world.runtime;

import com.chugalkhorbandar.domain.world.commands.WorldCommand;
import java.util.ArrayList;
import java.util.List;

public final class WorldCommandExecutor {

    private final WorldCommandHandlerRegistry registry;

    public WorldCommandExecutor(WorldCommandHandlerRegistry registry) {
        if (registry == null) {
            throw new WorldExecutionException("WorldCommandHandlerRegistry is required");
        }
        this.registry = registry;
    }

    public static WorldCommandExecutor createDefault() {
        return new WorldCommandExecutor(WorldCommandHandlerRegistry.createDefault());
    }

    public WorldRuntime execute(List<WorldCommand> commands) {
        if (commands == null) {
            throw new WorldExecutionException("Cannot execute null command list");
        }

        long startNanos = System.nanoTime();
        List<String> failures = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        WorldState state = WorldState.empty();
        int executed = 0;

        for (WorldCommand command : commands) {
            if (command == null) {
                throw new WorldExecutionException("Cannot execute null command");
            }
            state = registry.dispatch(state, command);
            executed++;
        }

        long durationMillis = (System.nanoTime() - startNanos) / 1_000_000;
        WorldExecutionReport report = new WorldExecutionReport(
                executed, durationMillis, failures, warnings, state.statistics(), true);
        return new WorldRuntime(state, report);
    }
}
