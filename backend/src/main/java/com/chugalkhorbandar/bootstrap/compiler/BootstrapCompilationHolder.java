package com.chugalkhorbandar.bootstrap.compiler;

import org.springframework.stereotype.Component;

@Component
public class BootstrapCompilationHolder {

    private BootstrapCompilation compilation;

    public void initialize(BootstrapCompilation compilation) {
        this.compilation = compilation;
    }

    public BootstrapCompilation getRequired() {
        if (compilation == null) {
            throw new IllegalStateException("Bootstrap compilation has not been performed yet");
        }
        return compilation;
    }
}
