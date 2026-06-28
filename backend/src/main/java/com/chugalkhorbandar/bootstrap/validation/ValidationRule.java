package com.chugalkhorbandar.bootstrap.validation;

import com.chugalkhorbandar.bootstrap.model.BootstrapWorld;
import com.chugalkhorbandar.bootstrap.model.ValidationIssue;
import java.util.List;

public interface ValidationRule {

    List<ValidationIssue> validate(BootstrapWorld world);
}
