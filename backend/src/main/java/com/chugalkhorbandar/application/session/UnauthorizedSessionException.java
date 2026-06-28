package com.chugalkhorbandar.application.session;

public final class UnauthorizedSessionException extends RuntimeException {

    public UnauthorizedSessionException() {
        super("Session is not active");
    }
}
