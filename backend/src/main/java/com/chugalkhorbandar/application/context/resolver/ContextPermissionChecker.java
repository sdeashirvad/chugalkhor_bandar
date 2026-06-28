package com.chugalkhorbandar.application.context.resolver;

import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSectionType;

public interface ContextPermissionChecker {

    boolean canAccess(ContextSectionType type, ContextReference reference);
}
