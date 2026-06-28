package com.chugalkhorbandar.application.context.resolver;

import com.chugalkhorbandar.application.context.ContextReference;
import com.chugalkhorbandar.application.context.ContextSectionType;
import org.springframework.stereotype.Component;

@Component
public class StubContextPermissionChecker implements ContextPermissionChecker {

    @Override
    public boolean canAccess(ContextSectionType type, ContextReference reference) {
        if (type == ContextSectionType.SECRET_MEMORY) {
            return false;
        }
        if ("memory".equals(reference.provider()) && "secret".equals(reference.attribute())) {
            return false;
        }
        return true;
    }
}
