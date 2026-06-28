package com.chugalkhorbandar.bootstrap;

import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.Manifest;
import com.chugalkhorbandar.bootstrap.model.ValidationReport;

public class BootstrapContext {

    private final BootstrapWorld world;
    private final ValidationReport report;

    public BootstrapContext(BootstrapWorld world, ValidationReport report) {
        this.world = world;
        this.report = report;
    }

    public BootstrapWorld getWorld() {
        return world;
    }

    public ValidationReport getReport() {
        return report;
    }

    public Manifest getManifest() {
        return world.manifest().orElseThrow(() -> new IllegalStateException("Manifest is not available"));
    }
}
