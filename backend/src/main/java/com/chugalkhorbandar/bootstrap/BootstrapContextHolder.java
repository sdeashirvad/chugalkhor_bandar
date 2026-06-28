package com.chugalkhorbandar.bootstrap;

import org.springframework.stereotype.Component;

@Component
public class BootstrapContextHolder {

    private BootstrapContext context;

    public void initialize(BootstrapContext context) {
        this.context = context;
    }

    public BootstrapContext getRequired() {
        if (context == null) {
            throw new IllegalStateException("Bootstrap has not been validated yet");
        }
        return context;
    }
}
