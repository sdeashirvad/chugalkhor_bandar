package com.chugalkhorbandar.bootstrap.typed;

import org.springframework.stereotype.Component;

@Component
public class BootstrapTypedWorldHolder {

    private BootstrapTypedWorld typedWorld;

    public void initialize(BootstrapTypedWorld typedWorld) {
        this.typedWorld = typedWorld;
    }

    public BootstrapTypedWorld getRequired() {
        if (typedWorld == null) {
            throw new IllegalStateException("Typed bootstrap world has not been loaded yet");
        }
        return typedWorld;
    }
}
