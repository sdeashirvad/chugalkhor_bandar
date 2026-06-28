package com.chugalkhorbandar.bootstrap.runtime;

import com.chugalkhorbandar.application.runtime.WorldRuntimeStatusHolder;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilation;
import com.chugalkhorbandar.bootstrap.compiler.BootstrapCompilationHolder;
import com.chugalkhorbandar.domain.world.commands.BootstrapToWorldCommandMapper;
import com.chugalkhorbandar.domain.world.commands.WorldCommand;
import com.chugalkhorbandar.domain.world.ports.WorldPersistenceService;
import com.chugalkhorbandar.domain.world.ports.WorldUnitOfWork;
import com.chugalkhorbandar.domain.world.runtime.WorldCommandExecutor;
import com.chugalkhorbandar.domain.world.runtime.WorldRuntime;
import com.chugalkhorbandar.ports.PersistenceProvider;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(6)
public class BootstrapRuntimeInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapRuntimeInitializer.class);

    private final BootstrapCompilationHolder compilationHolder;
    private final WorldPersistenceService persistenceService;
    private final PersistenceProvider persistenceProvider;
    private final WorldRuntimeStatusHolder runtimeStatusHolder;

    public BootstrapRuntimeInitializer(
            BootstrapCompilationHolder compilationHolder,
            WorldPersistenceService persistenceService,
            PersistenceProvider persistenceProvider,
            WorldRuntimeStatusHolder runtimeStatusHolder) {
        this.compilationHolder = compilationHolder;
        this.persistenceService = persistenceService;
        this.persistenceProvider = persistenceProvider;
        this.runtimeStatusHolder = runtimeStatusHolder;
    }

    @Override
    public void run(ApplicationArguments args) {
        BootstrapCompilation compilation = compilationHolder.getRequired();
        WorldRuntime runtime = initialize(compilation);
        persistRuntime(runtime);
        runtimeStatusHolder.markReady();
        logRuntime(runtime, compilation);
    }

    public WorldRuntime initialize(BootstrapCompilation compilation) {
        BootstrapToWorldCommandMapper mapper = new BootstrapToWorldCommandMapper();
        List<WorldCommand> commands = mapper.map(compilation);
        return WorldCommandExecutor.createDefault().execute(commands);
    }

    private void persistRuntime(WorldRuntime runtime) {
        WorldUnitOfWork unitOfWork = persistenceService.beginUnitOfWork();
        unitOfWork.begin();
        try {
            persistenceService.persist(runtime, unitOfWork);
            unitOfWork.commit();
        } catch (RuntimeException exception) {
            unitOfWork.rollback();
            throw exception;
        }
    }

    private void logRuntime(WorldRuntime runtime, BootstrapCompilation compilation) {
        log.info("Loading Bootstrap ........ PASSED");
        log.info("");
        log.info("Compiling World .......... PASSED");
        log.info("");
        log.info("Executing Commands ....... PASSED");
        log.info("");
        log.info("Persistence Provider ..... {}", persistenceProvider.getType().getDisplayName());
        log.info("");
        log.info("Running Flyway ........... PASSED");
        log.info("");
        log.info("Persisting World ......... PASSED");
        log.info("");
        log.info("World Commands .......... {}", compilation.report().totalCommands());
        log.info("");
        runtime.report().statistics().forEach((label, count) -> {
            if (count > 0) {
                logCount(label, count);
            }
        });
        log.info("");
        log.info("World Ready");
        log.info("");
    }

    private void logCount(String label, int count) {
        String dots = ".".repeat(Math.max(1, 18 - label.length()));
        log.info("{}{} {}", label, dots, count);
    }
}
