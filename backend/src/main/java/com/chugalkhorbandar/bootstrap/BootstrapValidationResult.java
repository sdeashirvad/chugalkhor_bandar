package com.chugalkhorbandar.bootstrap;

import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationReport;

public record BootstrapValidationResult(BootstrapWorld world, ValidationReport report) {}
