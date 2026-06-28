package com.chugalkhorbandar.adapters.persistence.postgres;

import org.testcontainers.DockerClientFactory;

public final class DockerTestSupport {

    private DockerTestSupport() {}

    public static boolean isDockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
