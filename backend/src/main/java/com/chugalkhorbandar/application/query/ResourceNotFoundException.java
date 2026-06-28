package com.chugalkhorbandar.application.query;

public final class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceType, String id) {
        super(resourceType + " not found: " + id);
    }
}
