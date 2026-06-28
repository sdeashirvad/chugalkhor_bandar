package com.chugalkhorbandar.bootstrap.compiler;

import com.chugalkhorbandar.bootstrap.typed.BootstrapTypedWorldHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class BootstrapCompilationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapCompilationRunner.class);

    private final BootstrapTypedWorldHolder typedWorldHolder;
    private final BootstrapCompilationHolder compilationHolder;

    public BootstrapCompilationRunner(
            BootstrapTypedWorldHolder typedWorldHolder, BootstrapCompilationHolder compilationHolder) {
        this.typedWorldHolder = typedWorldHolder;
        this.compilationHolder = compilationHolder;
    }

    @Override
    public void run(ApplicationArguments args) {
        BootstrapCompiler compiler = new BootstrapCompiler();
        BootstrapCompilation compilation = compiler.compile(typedWorldHolder.getRequired());
        compilationHolder.initialize(compilation);

        log.info("Bootstrap Compilation");
        log.info("");
        log.info("Commands Generated");
        log.info("");
        compilation.report().commandCountsByType().forEach((label, count) -> logCount(label, count));
        logCount("Total Commands", compilation.report().totalCommands());
        log.info("");
        log.info("Compilation Time . {} ms", compilation.report().compileDurationMillis());
        log.info("");
        log.info("Warnings . {}", compilation.warnings().size());
        log.info("");
    }

    private void logCount(String label, int count) {
        String dots = ".".repeat(Math.max(1, 18 - label.length()));
        log.info("{}{} {}", label, dots, count);
    }
}
