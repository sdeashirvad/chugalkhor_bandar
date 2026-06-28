package com.chugalkhorbandar.bootstrap.compiler;

import com.chugalkhorbandar.bootstrap.compiler.command.BootstrapCommand;
import java.util.List;

public record BootstrapCompilation(
        List<BootstrapCommand> commands, List<String> warnings, BootstrapCompilationReport report) {

    public BootstrapCompilation {
        commands = List.copyOf(commands);
        warnings = List.copyOf(warnings);
    }
}
