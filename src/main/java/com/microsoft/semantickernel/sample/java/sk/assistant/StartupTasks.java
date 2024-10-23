package com.microsoft.semantickernel.sample.java.sk.assistant;


import io.quarkus.runtime.Startup;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tasks to be performed when the application starts
 */
@Startup
public class StartupTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupTasks.class);

    private final SemanticKernelProvider semanticKernelProvider;

    @Inject
    public StartupTasks(SemanticKernelProvider semanticKernelProvider) {
        this.semanticKernelProvider = semanticKernelProvider;
    }

    @Startup
    public void run() {
        semanticKernelProvider.init();

        LOGGER.info("===STARTUP COMPLETE===");
    }
}
